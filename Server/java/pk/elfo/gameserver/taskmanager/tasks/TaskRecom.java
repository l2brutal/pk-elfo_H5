package pk.elfo.gameserver.taskmanager.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;

import pk.elfo.L2DatabaseFactory;
import pk.elfo.gameserver.taskmanager.Task;
import pk.elfo.gameserver.taskmanager.TaskManager;
import pk.elfo.gameserver.taskmanager.TaskTypes;
import pk.elfo.gameserver.taskmanager.TaskManager.ExecutedTask;

public class TaskRecom extends Task
{
	private static final String NAME = "recommendations";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left=?, time_left=?, rec_have=0 WHERE rec_have <=  20"))
			{
				ps.setInt(1, 0); // Rec left = 0
				ps.setInt(2, 3600000); // Timer = 1 hour
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement("UPDATE character_reco_bonus SET rec_left=?, time_left=?, rec_have=GREATEST(rec_have-20,0) WHERE rec_have > 20"))
			{
				ps.setInt(1, 0); // Rec left = 0
				ps.setInt(2, 3600000); // Timer = 1 hour
				ps.execute();
			}
		}
		catch (Exception e)
		{
			_log.severe(getClass().getSimpleName() + ": Could not reset Recommendations System: " + e);
		}
		_log.info("Recommendations System reseted");
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}