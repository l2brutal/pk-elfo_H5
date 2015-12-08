/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package pk.elfo.gameserver.model.skills.funcs.formulas;

import pk.elfo.gameserver.model.skills.funcs.Func;
import pk.elfo.gameserver.model.stats.BaseStats;
import pk.elfo.gameserver.model.stats.Env;
import pk.elfo.gameserver.model.stats.Stats;

/**
 * @author UnAfraid
 */
public class FuncMoveSpeed extends Func
{
	private static final FuncMoveSpeed _fms_instance = new FuncMoveSpeed();
	
	public static Func getInstance()
	{
		return _fms_instance;
	}
	
	private FuncMoveSpeed()
	{
		super(Stats.RUN_SPEED, 0x30, null);
	}
	
	@Override
	public void calc(Env env)
	{
		env.mulValue(BaseStats.DEX.calcBonus(env.getCharacter()));
	}
}