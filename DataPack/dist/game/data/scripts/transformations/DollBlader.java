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

public class DollBlader extends L2Transformation
{
	private static final int[] SKILLS =
	{
		752,
		753,
		754,
		5491,
		619
	};
	
	public DollBlader()
	{
		// id, colRadius, colHeight
		super(7, 6, 12);
	}
	
	@Override
	public void onTransform()
	{
		if ((getPlayer().getTransformationId() != 7) || getPlayer().isCursedWeaponEquipped())
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
		// Doll Blader Clairvoyance
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(754, 1), false, false);
		
		if (getPlayer().getLevel() >= 76)
		{
			// Doll Blader Sting (up to 3 levels)
			getPlayer().removeSkill(SkillTable.getInstance().getInfo(752, 3), false);
			// Doll Blader Throwing Knife (up to 3 levels)
			getPlayer().removeSkill(SkillTable.getInstance().getInfo(753, 3), false);
		}
		else if (getPlayer().getLevel() >= 73)
		{
			// Doll Blader Sting (up to 3 levels)
			getPlayer().removeSkill(SkillTable.getInstance().getInfo(752, 2), false);
			// Doll Blader Throwing Knife (up to 3 levels)
			getPlayer().removeSkill(SkillTable.getInstance().getInfo(753, 2), false);
		}
		else
		{
			// Doll Blader Sting (up to 3 levels)
			getPlayer().removeSkill(SkillTable.getInstance().getInfo(752, 1), false);
			// Doll Blader Throwing Knife (up to 3 levels)
			getPlayer().removeSkill(SkillTable.getInstance().getInfo(753, 1), false);
		}
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Transform Dispel
		getPlayer().removeSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(EMPTY_ARRAY);
	}
	
	public void transformedSkills()
	{
		// Doll Blader Clairvoyance
		getPlayer().addSkill(SkillTable.getInstance().getInfo(754, 1), false);
		
		if (getPlayer().getLevel() >= 76)
		{
			// Doll Blader Sting (up to 3 levels)
			getPlayer().addSkill(SkillTable.getInstance().getInfo(752, 3), false);
			// Doll Blader Throwing Knife (up to 3 levels)
			getPlayer().addSkill(SkillTable.getInstance().getInfo(753, 3), false);
		}
		else if (getPlayer().getLevel() >= 73)
		{
			// Doll Blader Sting (up to 3 levels)
			getPlayer().addSkill(SkillTable.getInstance().getInfo(752, 2), false);
			// Doll Blader Throwing Knife (up to 3 levels)
			getPlayer().addSkill(SkillTable.getInstance().getInfo(753, 2), false);
		}
		else if (getPlayer().getLevel() >= 70)
		{
			// Doll Blader Sting (up to 3 levels)
			getPlayer().addSkill(SkillTable.getInstance().getInfo(752, 1), false);
			// Doll Blader Throwing Knife (up to 3 levels)
			getPlayer().addSkill(SkillTable.getInstance().getInfo(753, 1), false);
		}
		// Decrease Bow/Crossbow Attack Speed
		getPlayer().addSkill(SkillTable.getInstance().getInfo(5491, 1), false);
		// Transform Dispel
		getPlayer().addSkill(SkillTable.getInstance().getInfo(619, 1), false);
		
		getPlayer().setTransformAllowedSkills(SKILLS);
	}
	
	public static void main(String[] args)
	{
		TransformationManager.getInstance().registerTransformation(new DollBlader());
	}
}
