/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package hellbound.Deltuva;

import pk.elfo.gameserver.model.actor.L2Npc;
import pk.elfo.gameserver.model.actor.instance.L2PcInstance;
import pk.elfo.gameserver.model.quest.Quest;
import pk.elfo.gameserver.model.quest.QuestState;
import quests.Q00132_MatrasCuriosity.Q00132_MatrasCuriosity;

/**
 * @author GKR
 */
public class Deltuva extends Quest
{
	private static final int DELTUVA = 32313;
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (event.equalsIgnoreCase("teleport"))
		{
			final QuestState hostQuest = player.getQuestState(Q00132_MatrasCuriosity.class.getSimpleName());
			if ((hostQuest == null) || !hostQuest.isCompleted())
			{
				htmltext = "32313-02.htm";
			}
			else
			{
				player.teleToLocation(17934, 283189, -9701);
			}
		}
		return htmltext;
	}
	
	public Deltuva(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(DELTUVA);
		addTalkId(DELTUVA);
	}
	
	public static void main(String[] args)
	{
		new Deltuva(-1, "Deltuva", "hellbound");
	}
}