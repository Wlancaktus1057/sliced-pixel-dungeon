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

package com.zrp200.rkpd2.actors.hero.abilities.huntress;

import com.watabou.utils.Random;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Buff;
import com.zrp200.rkpd2.actors.buffs.Invisibility;
import com.zrp200.rkpd2.actors.hero.Hero;
import com.zrp200.rkpd2.actors.hero.Talent;
import com.zrp200.rkpd2.actors.hero.abilities.ArmorAbility;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.items.armor.ClassArmor;
import com.zrp200.rkpd2.items.weapon.missiles.Shuriken;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.mechanics.ConeAOE;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.sprites.MissileSprite;
import com.zrp200.rkpd2.utils.GLog;
import com.watabou.utils.Callback;

import java.util.HashSet;

public class SpectralBlades extends ArmorAbility {

	{
		baseChargeUse = 25f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		if (Actor.findChar(target) == hero){
			GLog.w(Messages.get(this, "self_target"));
			return;
		}

		Ballistica b = new Ballistica(hero.pos, target, Ballistica.WONT_STOP);
		final HashSet<Char> targets = new HashSet<>();

		int wallPenetration = 1+2*hero.pointsInTalent(Talent.PROJECTING_BLADES);
		Char enemy = findChar(b, hero, wallPenetration, targets);

		if (enemy == null){
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		targets.add(enemy);

		if (hero.canHaveTalent(Talent.FAN_OF_BLADES)){
			int degrees = Math.max(30, 45*hero.pointsInTalent(Talent.FAN_OF_BLADES));
			ConeAOE cone = new ConeAOE(b, degrees);
			for (Ballistica ray : cone.rays){
				// 1/3/5/7/9 up from 0/2/4/6/8
				Char toAdd = findChar(ray, hero, wallPenetration, targets);
				if (toAdd != null && hero.fieldOfView[toAdd.pos]){
					targets.add(toAdd);
				}
			}
			// 1/2-3/4/5-6/7, up from 0/1/2/3/4
			int additionalTargets = Random.round( .5f*( 3*hero.shiftedPoints(Talent.FAN_OF_BLADES) - 1 ) );
			while (targets.size() > 1 + additionalTargets){
				Char furthest = null;
				for (Char ch : targets){
					if (furthest == null){
						furthest = ch;
					} else if (Dungeon.level.trueDistance(enemy.pos, ch.pos) >
							Dungeon.level.trueDistance(enemy.pos, furthest.pos)){
						furthest = ch;
					}
				}
				targets.remove(furthest);
			}
		}

		armor.charge -= chargeUse(hero);
		Item.updateQuickslot();

		Item proto = new Shuriken();

		final HashSet<Callback> callbacks = new HashSet<>();

		for (Char ch : targets) {
			Callback callback = new Callback() {
				@Override
				public void call() {
					float dmgMulti = ch == enemy ? 1f : 0.5f;
					float accmulti = 1 + 1/3f*hero.shiftedPoints(Talent.PROJECTING_BLADES);
					if (hero.hasTalent(Talent.SPIRIT_BLADES)){
						Buff.affect(hero, Talent.SpiritBladesTracker.class, 0f);
					}
					hero.attack( ch, dmgMulti, 0, accmulti );
					callbacks.remove( this );
					if (callbacks.isEmpty()) {
						Invisibility.dispel();
						hero.spendAndNext( hero.attackDelay() );
					}
				}
			};

			MissileSprite m = ((MissileSprite)hero.sprite.parent.recycle( MissileSprite.class ));
			m.reset( hero.sprite, ch.pos, proto, callback );
			m.hardlight(0.6f, 1f, 1f);
			m.alpha(0.8f);

			callbacks.add( callback );
		}

		hero.sprite.zap( enemy.pos );
		hero.busy();
	}

	private Char findChar(Ballistica path, Hero hero, int wallPenetration, HashSet<Char> existingTargets){
		for (int cell : path.path){
			Char ch = Actor.findChar(cell);
			if (ch != null){
				if (ch == hero || existingTargets.contains(ch)){
					continue;
				} else if (ch.alignment != Char.Alignment.ALLY){
					return ch;
				} else {
					return null;
				}
			}
			if (Dungeon.level.solid[cell]){
				wallPenetration--;
				if (wallPenetration < 0){
					return null;
				}
			}
		}
		return null;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.FAN_OF_BLADES, Talent.PROJECTING_BLADES, Talent.SPIRIT_BLADES, Talent.HEROIC_ENERGY};
	}
}
