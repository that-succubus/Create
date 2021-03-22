package com.simibubi.create;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import com.simibubi.create.foundation.utility.Lang;

public enum AllSoundEvents implements DataProvider {

	CUCKOO_PIG("pigclock"),
	CUCKOO_CREEPER("creeperclock"),

	SCHEMATICANNON_LAUNCH_BLOCK(SoundEvents.ENTITY_GENERIC_EXPLODE),
	SCHEMATICANNON_FINISH(SoundEvents.BLOCK_NOTE_BLOCK_BELL),
	SLIME_ADDED(SoundEvents.BLOCK_SLIME_BLOCK_PLACE),
	MECHANICAL_PRESS_ACTIVATION(SoundEvents.BLOCK_ANVIL_LAND),
	MECHANICAL_PRESS_ITEM_BREAK(SoundEvents.ENTITY_ITEM_BREAK),
	BLOCKZAPPER_PLACE(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
	BLOCKZAPPER_CONFIRM(SoundEvents.BLOCK_NOTE_BLOCK_BELL),
	BLOCKZAPPER_DENY(SoundEvents.BLOCK_NOTE_BLOCK_BASS),
	BLOCK_FUNNEL_EAT(SoundEvents.ENTITY_GENERIC_EAT),
	BLAZE_MUNCH(SoundEvents.ENTITY_GENERIC_EAT)

	;

	String id;
	SoundEvent event, child;
	private DataGenerator generator;

	// For adding our own sounds at assets/create/sounds/name.ogg
	AllSoundEvents() {
		id = Lang.asId(name());
	}

	AllSoundEvents(String name) {
		id = name;
	}

	// For wrapping a existing sound with new subtitle
	AllSoundEvents(SoundEvent child) {
		this();
		this.child = child;
	}

	// subtitles are taken from the lang file (create.subtitle.sound_event_name)

	public SoundEvent get() {
		return event;
	}

	private String getEventName() {
		return id;
	}

	public AllSoundEvents generator(DataGenerator generator) {
		this.generator = generator;
		return this;
	}

	public static void register() {
		for (AllSoundEvents entry : values()) {
			Identifier rec = new Identifier(Create.ID, entry.getEventName());
			SoundEvent sound = new SoundEvent(rec);
			Registry.register(Registry.SOUND_EVENT, rec, sound);
			entry.event = sound;
		}
	}

	public void generate(Path path, DataCache cache) {
		Gson GSON = (new GsonBuilder()).setPrettyPrinting()
			.disableHtmlEscaping()
			.create();
		path = path.resolve("assets/create");

		try {
			JsonObject json = new JsonObject();
			for (AllSoundEvents soundEvent : values()) {
				JsonObject entry = new JsonObject();
				JsonArray arr = new JsonArray();
				if (soundEvent.child != null) {
					// wrapper
					JsonObject s = new JsonObject();
					s.addProperty("name", soundEvent.child.getId()
						.toString());
					s.addProperty("type", "event");
					arr.add(s);
				} else {
					// own sound
					arr.add(Create.ID + ":" + soundEvent.getEventName());
				}
				entry.add("sounds", arr);
				entry.addProperty("subtitle", Create.ID + ".subtitle." + soundEvent.getEventName());
				json.add(soundEvent.getEventName(), entry);
			}
			DataProvider.writeToPath(GSON, cache, json, path.resolve("sounds.json"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run(DataCache cache) throws IOException {
		generate(generator.getOutput(), cache);
	}

	@Override
	public String getName() {
		return "Create's Custom Sound: " + name();
	}
}
