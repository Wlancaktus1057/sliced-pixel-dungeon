/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.alchemist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ExoskeletonBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Exoskeleton extends ArmorAbility {

	{
		baseChargeUse = 25f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		Buff.affect(hero, ExoskeletonBuff.class).set(ExoskeletonBuff.DURATION, hero.belongings.armor.tier);
		if (hero.hasTalent(Talent.DEPLOY_BARRIER)) {
			Buff.affect(hero, Barrier.class).setShield((int)
					((hero.pointsInTalent(Talent.DEPLOY_BARRIER) * 0.15f) * hero.HT + 1));
		}

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.PUFF);
		CellEmitter.center(hero.pos).burst(Speck.factory(Speck.STEAM_BLAST), 10);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.next();

	}

	public static void proc(Armor armor, Char defender) {
		// 20%, 25%, 33%, 50%
		if (Random.Int(5 - Dungeon.hero.pointsInTalent(Talent.KINETIC_CHARGER)) == 0) {
			((ClassArmor) armor).charge++;
			defender.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
		}
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HYDRAULIC_PISTON, Talent.DEPLOY_BARRIER, Talent.KINETIC_CHARGER, Talent.HEROIC_ENERGY};
	}
}
