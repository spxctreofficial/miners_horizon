package com.github.draylar.miners_horizon.common.blocks;

import com.github.draylar.miners_horizon.MinersHorizon;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class MinerPortalBlock extends PortalBlock
{
    public MinerPortalBlock()
    {
        super(FabricBlockSettings.copy(Blocks.NETHER_PORTAL).build());
    }

    @Override
    public void onEntityCollision(BlockState stateIn, World worldIn, BlockPos posIn, Entity entityIn)
    {
        if(!worldIn.isClient() && entityIn.isSneaking())
        {
            if(entityIn.dimension == MinersHorizon.FABRIC_WORLD) entityIn.changeDimension(DimensionType.OVERWORLD);
            else entityIn.changeDimension(MinersHorizon.FABRIC_WORLD);
        }
    }

    @Override
    public boolean method_10352(IWorld worldIn, BlockPos pos)
    {
        PortalSize portalSizeX = new PortalSize(worldIn, pos, Direction.Axis.X);

        if (portalSizeX.isValid() && portalSizeX.portalBlockCount == 0)
        {
            portalSizeX.placePortalBlocks();
            return true;
        }

        else
        {
            PortalSize portalSizeZ = new PortalSize(worldIn, pos, Direction.Axis.Z);

            if (portalSizeZ.isValid() && portalSizeZ.portalBlockCount == 0)
            {
                portalSizeZ.placePortalBlocks();
                return true;
            }

            else return false;
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState blockState_2, IWorld iWorld_1, BlockPos pos, BlockPos blockPos_2)
    {
        Direction.Axis axis = direction.getAxis();
        Direction.Axis axis2 = blockState.get(AXIS);

        boolean boolean_1 = axis2 != axis && axis.isHorizontal();
        return !boolean_1 && blockState_2.getBlock() != this && !(new PortalSize(iWorld_1, pos, axis2)).method_10362() ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(blockState, direction, blockState_2, iWorld_1, pos, blockPos_2);
    }

    @Override
    public BlockPattern.Result findPortal(IWorld worldIn, BlockPos p_181089_2_)
    {
        Direction.Axis enumfacing$axis = Direction.Axis.Z;
        PortalSize blockportal$size = new PortalSize(worldIn, p_181089_2_, Direction.Axis.X);
        LoadingCache<BlockPos, CachedBlockPosition> loadingcache = BlockPattern.makeCache(worldIn, true);

        if (!blockportal$size.isValid())
        {
            enumfacing$axis = Direction.Axis.X;
            blockportal$size = new PortalSize(worldIn, p_181089_2_, Direction.Axis.Z);
        }

        if (!blockportal$size.isValid())
        {
            return new BlockPattern.Result(p_181089_2_, Direction.NORTH, Direction.UP, loadingcache, 1, 1, 1);
        }
        else
        {
            int[] aint = new int[Direction.AxisDirection.values().length];
            Direction enumfacing = blockportal$size.rightDir.rotateYCounterclockwise();
            BlockPos blockpos = blockportal$size.bottomLeft.up(blockportal$size.getHeight() - 1);

            for (Direction.AxisDirection enumfacing$axisdirection : Direction.AxisDirection.values())
            {
                BlockPattern.Result blockpattern$patternhelper = new BlockPattern.Result(enumfacing.getDirection() == enumfacing$axisdirection ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), Direction.get(enumfacing$axisdirection, enumfacing$axis), Direction.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);

                for (int i = 0; i < blockportal$size.getWidth(); ++i)
                {
                    for (int j = 0; j < blockportal$size.getHeight(); ++j)
                    {
                        CachedBlockPosition blockworldstate = blockpattern$patternhelper.translate(i, j, 1);

                        if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getMaterial() != Material.AIR)
                        {
                            ++aint[enumfacing$axisdirection.ordinal()];
                        }
                    }
                }
            }

            Direction.AxisDirection enumfacing$axisdirection1 = Direction.AxisDirection.POSITIVE;

            for (Direction.AxisDirection enumfacing$axisdirection2 : Direction.AxisDirection.values())
            {
                if (aint[enumfacing$axisdirection2.ordinal()] < aint[enumfacing$axisdirection1.ordinal()])
                {
                    enumfacing$axisdirection1 = enumfacing$axisdirection2;
                }
            }

            return new BlockPattern.Result(enumfacing.getDirection() == enumfacing$axisdirection1 ? blockpos : blockpos.offset(blockportal$size.rightDir, blockportal$size.getWidth() - 1), Direction.get(enumfacing$axisdirection1, enumfacing$axis), Direction.UP, loadingcache, blockportal$size.getWidth(), blockportal$size.getHeight(), 1);
        }
    }

    public static class PortalSize
    {

        private final IWorld world;
        private final Direction.Axis axis;
        final Direction rightDir;
        final Direction leftDir;
        int portalBlockCount;
        BlockPos bottomLeft;
        int height;
        int width;

        PortalSize(IWorld worldIn, BlockPos position, Direction.Axis axis)
        {
            this.world = worldIn;
            this.axis = axis;

            if (axis == Direction.Axis.X)
            {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            }
            else
            {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }

            for (BlockPos blockpos = position; position.getY() > blockpos.getY() - 21 && position.getY() > 0 && this.isEmptyBlock(worldIn.getBlockState(position.down()).getBlock()); position = position.down())
                ;

            int i = this.getDistanceUntilEdge(position, this.leftDir) - 1;

            if (i >= 0)
            {
                this.bottomLeft = position.offset(this.leftDir, i);
                this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);

                if (this.width < 2 || this.width > 21)
                {
                    this.bottomLeft = null;
                    this.width = 0;
                }
            }

            if (this.bottomLeft != null)
            {
                this.height = this.calculatePortalHeight();
            }
        }

        int getDistanceUntilEdge(BlockPos position, Direction axis)
        {
            int i;

            for (i = 0; i < 22; ++i)
            {
                BlockPos blockpos = position.offset(axis, i);

                if (!this.isEmptyBlock(this.world.getBlockState(blockpos).getBlock()) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.CHISELED_STONE_BRICKS)
                {
                    break;
                }
            }

            Block block = this.world.getBlockState(position.offset(axis, i)).getBlock();
            return block == Blocks.CHISELED_STONE_BRICKS ? i : 0;
        }

        int getHeight()
        {
            return this.height;
        }

        int getWidth()
        {
            return this.width;
        }

        int calculatePortalHeight()
        {
            label24:

            for (this.height = 0; this.height < 21; ++this.height)
            {
                for (int i = 0; i < this.width; ++i)
                {
                    BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    Block block = this.world.getBlockState(blockpos).getBlock();

                    if (!this.isEmptyBlock(block))
                    {
                        break label24;
                    }

                    if (block == com.github.draylar.miners_horizon.common.Blocks.MINER_PORTAL)
                    {
                        ++this.portalBlockCount;
                    }

                    if (i == 0)
                    {
                        block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();

                        if (block != Blocks.CHISELED_STONE_BRICKS)
                        {
                            break label24;
                        }
                    }
                    else if (i == this.width - 1)
                    {
                        block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();

                        if (block != Blocks.CHISELED_STONE_BRICKS)
                        {
                            break label24;
                        }
                    }
                }
            }

            for (int j = 0; j < this.width; ++j)
            {
                if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != Blocks.CHISELED_STONE_BRICKS)
                {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3)
            {
                return this.height;
            }
            else
            {
                this.bottomLeft = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        boolean isEmptyBlock(Block blockIn)
        {
            return blockIn.getDefaultState().getMaterial() == Material.AIR || blockIn == Blocks.FIRE || blockIn == com.github.draylar.miners_horizon.common.Blocks.MINER_PORTAL;
        }

        boolean isValid()
        {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        void placePortalBlocks()
        {
            for (int i = 0; i < this.width; ++i)
            {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

                for (int j = 0; j < this.height; ++j)
                {
                    this.world.setBlockState(blockpos.up(j), com.github.draylar.miners_horizon.common.Blocks.MINER_PORTAL.getDefaultState().with(AXIS, this.axis), 2);
                }
            }
        }

        private boolean method_10361()
        {
            return this.portalBlockCount >= this.height * this.width;
        }

        boolean method_10362()
        {
            return this.isValid() && this.method_10361();
        }

    }
}
