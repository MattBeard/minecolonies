package com.minecolonies.tileentities;

import com.minecolonies.entity.EntityCitizen;
import com.minecolonies.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.UUID;

public abstract class TileEntityHutWorker extends TileEntityHut
{
    private UUID workerID;

    public abstract String getJobName();

    public abstract EntityCitizen createWorker();

    public void bindWorker(EntityCitizen worker)
    {
        workerID = worker.getUniqueID();
        worker.setWorkHut(this);
    }

    public void unbindWorker(EntityCitizen worker)
    {
        workerID = null;
        worker.setWorkHut(null);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if(worldObj.isRemote) return;

        if(!hasWorker() && this.getTownHall() != null)
        {
            addJoblessCitizens(this.getTownHall());
        }
    }

    public void addJoblessCitizens(TileEntityTownHall tileEntityTownHall)
    {
        List<UUID> citizens = tileEntityTownHall.getCitizens();

        List<Entity> entityCitizens = Utils.getEntitiesFromUUID(worldObj, citizens);
        if(entityCitizens != null)
        {
            for(Entity entity : entityCitizens)
            {
                if(entity instanceof EntityCitizen)
                {
                    EntityCitizen entityCitizen = (EntityCitizen) entity;
                    if(entityCitizen.getJob().equals("Citizen") && !hasWorker())
                    {
                        entityCitizen.addToWorkHut(this);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        if(compound.hasKey("workerID"))
        {
            this.workerID = UUID.fromString(compound.getString("workerID"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if(hasWorker())
        {
            compound.setString("workerID", workerID.toString());
        }
    }

    @Override
    public void breakBlock()
    {
        if(hasWorker())
        {
            EntityCitizen worker = (EntityCitizen) Utils.getEntityFromUUID(worldObj, workerID);
            if(worker != null)
            {
                worker.removeFromWorkHut();
            }
        }
    }

    public boolean hasWorker()
    {
        return workerID != null;
    }

    public UUID getWorkerID()
    {
        return workerID;
    }
}