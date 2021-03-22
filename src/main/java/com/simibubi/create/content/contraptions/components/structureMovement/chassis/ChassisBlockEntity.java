package com.simibubi.create.content.contraptions.components.structureMovement.chassis;

import static net.minecraft.state.property.Properties.AXIS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementTraits;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.BulkScrollValueBehaviour;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;

public class ChassisBlockEntity extends SmartBlockEntity {

	ScrollValueBehaviour range;

	public ChassisBlockEntity() {
		super(AllBlockEntities.CHASSIS);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		int max = Create.getConfig().kinetics.maxChassisRange;
		range = new BulkScrollValueBehaviour(Lang.translate("generic.range"), this, new CenteredSideValueBoxTransform(),
				te -> ((ChassisBlockEntity) te).collectChassisGroup());
		range.requiresWrench();
		range.between(1, max);
		range.withClientCallback(
						//i -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
							i -> ChassisRangeDisplay.display(this)
						//)
		);

		range.value = max / 2;
		behaviours.add(range);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (getCachedState().getBlock() instanceof RadialChassisBlock)
			range.setLabel(Lang.translate("generic.radius"));
	}

	public int getRange() {
		return range.getValue();
	}

	public List<BlockPos> getIncludedBlockPositions(Direction forcedMovement, boolean visualize) {
		if (!(getCachedState().getBlock() instanceof AbstractChassisBlock))
			return Collections.emptyList();
		return isRadial() ? getIncludedBlockPositionsRadial(forcedMovement, visualize)
				: getIncludedBlockPositionsLinear(forcedMovement, visualize);
	}

	protected boolean isRadial() {
		return world.getBlockState(pos).getBlock() instanceof RadialChassisBlock;
	}

	public List<ChassisBlockEntity> collectChassisGroup() {
		Queue<BlockPos> frontier = new LinkedList<>();
		List<ChassisBlockEntity> collected = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		frontier.add(pos);
		while (!frontier.isEmpty()) {
			BlockPos current = frontier.poll();
			if (visited.contains(current))
				continue;
			visited.add(current);
			BlockEntity tileEntity = world.getBlockEntity(current);
			if (tileEntity instanceof ChassisBlockEntity) {
				ChassisBlockEntity chassis = (ChassisBlockEntity) tileEntity;
				collected.add(chassis);
				visited.add(current);
				chassis.addAttachedChasses(frontier, visited);
			}
		}
		return collected;
	}

	public boolean addAttachedChasses(Queue<BlockPos> frontier, Set<BlockPos> visited) {
		BlockState state = getCachedState();
		if (!(state.getBlock() instanceof AbstractChassisBlock))
			return false;
		Axis axis = state.get(AbstractChassisBlock.AXIS);
		if (isRadial()) {

			// Collect chain of radial chassis
			for (int offset : new int[] { -1, 1 }) {
				Direction direction = Direction.get(AxisDirection.POSITIVE, axis);
				BlockPos currentPos = pos.offset(direction, offset);
				if (!world.canSetBlock(currentPos))
					return false;

				BlockState neighbourState = world.getBlockState(currentPos);
				if (!AllBlocks.RADIAL_CHASSIS.getStateManager().getStates().contains(neighbourState))
					continue;
				if (axis != neighbourState.get(Properties.AXIS))
					continue;
				if (!visited.contains(currentPos))
					frontier.add(currentPos);
			}

			return true;
		}

		// Collect group of connected linear chassis
		for (Direction offset : Iterate.directions) {
			if (offset.getAxis() == axis)
				continue;
			BlockPos current = pos.offset(offset);
			if (visited.contains(current))
				continue;
			if (!world.canSetBlock(current))
				return false;

			BlockState neighbourState = world.getBlockState(current);
			if (!LinearChassisBlock.isChassis(neighbourState))
				continue;
			if (!LinearChassisBlock.sameKind(state, neighbourState))
				continue;
			if (neighbourState.get(AXIS) != axis)
				continue;

			frontier.add(current);
		}

		return true;
	}

	private List<BlockPos> getIncludedBlockPositionsLinear(Direction forcedMovement, boolean visualize) {
		List<BlockPos> positions = new ArrayList<>();
		BlockState state = getCachedState();
		AbstractChassisBlock block = (AbstractChassisBlock) state.getBlock();
		Axis axis = state.get(AbstractChassisBlock.AXIS);
		Direction facing = Direction.get(AxisDirection.POSITIVE, axis);
		int chassisRange = visualize ? range.scrollableValue : getRange();

		for (int offset : new int[] { 1, -1 }) {
			if (offset == -1)
				facing = facing.getOpposite();
			boolean sticky = state.get(block.getGlueableSide(state, facing));
			for (int i = 1; i <= chassisRange; i++) {
				BlockPos current = pos.offset(facing, i);
				BlockState currentState = world.getBlockState(current);

				if (forcedMovement != facing && !sticky)
					break;

				// Ignore replaceable Blocks and Air-like
				if (!BlockMovementTraits.movementNecessary(currentState, world, current))
					break;
				if (BlockMovementTraits.isBrittle(currentState))
					break;

				positions.add(current);

				if (BlockMovementTraits.notSupportive(currentState, facing))
					break;
			}
		}

		return positions;
	}

	private List<BlockPos> getIncludedBlockPositionsRadial(Direction forcedMovement, boolean visualize) {
		List<BlockPos> positions = new ArrayList<>();
		BlockState state = world.getBlockState(pos);
		Axis axis = state.get(AbstractChassisBlock.AXIS);
		AbstractChassisBlock block = (AbstractChassisBlock) state.getBlock();
		int chassisRange = visualize ? range.scrollableValue : getRange();

		for (Direction facing : Iterate.directions) {
			if (facing.getAxis() == axis)
				continue;
			if (!state.get(block.getGlueableSide(state, facing)))
				continue;

			BlockPos startPos = pos.offset(facing);
			List<BlockPos> localFrontier = new LinkedList<>();
			Set<BlockPos> localVisited = new HashSet<>();
			localFrontier.add(startPos);

			while (!localFrontier.isEmpty()) {
				BlockPos searchPos = localFrontier.remove(0);
				BlockState searchedState = world.getBlockState(searchPos);

				if (localVisited.contains(searchPos))
					continue;
				if (!searchPos.isWithinDistance(pos, chassisRange + .5f))
					continue;
				if (!BlockMovementTraits.movementNecessary(searchedState, world, searchPos))
					continue;
				if (BlockMovementTraits.isBrittle(searchedState))
					continue;

				localVisited.add(searchPos);
				if (!searchPos.equals(pos))
					positions.add(searchPos);

				for (Direction offset : Iterate.directions) {
					if (offset.getAxis() == axis)
						continue;
					if (searchPos.equals(pos) && offset != facing)
						continue;
					if (BlockMovementTraits.notSupportive(searchedState, offset))
						continue;

					localFrontier.add(searchPos.offset(offset));
				}
			}
		}

		return positions;
	}

}
