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
package transformations;

import pk.elfo.gameserver.datatables.SkillTable;
import pk.elfo.gameserver.instancemanager.TransformationManager;
import pk.elfo.gameserver.model.L2Transformation;

public class OnyxBeast extends L2Transformation
{
	private static final int[] SKILLS =
	{
		584,
		585,
		5491,
		619
	};
	
	public OnyxBeast()
	{
		// id, colRadius, colHeight
		super(1, 14, 14.5);
	}
	
	@Override
	public void onTransform()
	{
		if ((getPlayer().getTransformationId() != 1) || getPlayer().isCursedWeaponEquipped())
		{
			return;
		}
		
		transformedSkills();
	}
	
	@Override
	public void onUntransform()
	{
		removeSkills();
	}
	
	public void removeSkills()
	{
		// Power Claw
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(584, 1), false);
		// Fast Moving
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(585, 1), false, false);
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Transform Dispel
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}
	
	public void transformedSkills()
	{
		// Power Claw
		getPlayer().addSkill(SkillTable.getInstance().getInfo(584, 1), false);
		// Fast Moving
		getPlayer().addSkill(SkillTable.getInstance().getInfo(585, 1), false);
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().addSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Transform Dispel
		getPlayer().addSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(SKILLS);
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new OnyxBeast());
	}
}
