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

package com.zrp200.rkpd2.actors.mobs;

import com.zrp200.rkpd2.Assets;
import com.zrp200.rkpd2.Dungeon;
import com.zrp200.rkpd2.actors.Actor;
import com.zrp200.rkpd2.actors.Char;
import com.zrp200.rkpd2.actors.buffs.Cripple;
import com.zrp200.rkpd2.effects.Chains;
import com.zrp200.rkpd2.effects.Pushing;
import com.zrp200.rkpd2.items.Generator;
import com.zrp200.rkpd2.items.Item;
import com.zrp200.rkpd2.mechanics.Ballistica;
import com.zrp200.rkpd2.messages.Messages;
import com.zrp200.rkpd2.scenes.GameScene;
import com.zrp200.rkpd2.sprites.GuardSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Guard extends Mob {

	//they can only use their chains once
	private boolean chainsUsed = false;

	{
		spriteClass = GuardSprite.class;

		HP = HT = 40;
		defenseSkill = 10;

		EXP = 7;
		maxLvl = 14;

		loot = Generator.Category.ARMOR;
		lootChance = 0.2f; //by default, see rollToDropLoot()

		properties.add(Property.UNDEAD);
		
		HUNTING = new Hunting();
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(4, 12);
	}

	private boolean chain(int target){
		if (chainsUsed || enemy.properties().contains(Property.IMMOVABLE))
			return false;

		Ballistica chain = new Ballistica(pos, target, Ballistica.PROJECTILE);

		if (chain.collisionPos != enemy.pos
				|| chain.path.size() < 2
				|| Dungeon.level.pit[chain.path.get(1)])
			return false;
		else {
			int newPos = -1;
			for (int i : chain.subPath(1, chain.dist)){
				if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
					newPos = i;
					break;
				}
			}

			if (newPos == -1){
				return false;
			} else {
				final int newPosFinal = newPos;
				this.target = newPos;
				yell( Messages.get(this, "scorpion") );
				new Item().throwSound();
				Sample.INSTANCE.play( Assets.Sounds.CHAINS );
				sprite.parent.add(new Chains(sprite.center(), enemy.sprite.center(), new Callback() {
					public void call() {
						Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback(){
							public void call() {
								enemy.pos = newPosFinal;
								Dungeon.level.occupyCell(enemy);
								Cripple.prolong(enemy, Cripple.class, 4f);
								if (enemy == Dungeon.hero) {
									Dungeon.hero.interrupt();
									Dungeon.observe();
									GameScene.updateFog();
								}
							}
						}), -1);
						next();
					}
				}));
			}
		}
		chainsUsed = true;
		return true;
	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 7);
	}

	@Override
	public void rollToDropLoot() {
		//each drop makes future drops 1/2 as likely
		// so loot chance looks like: 1/5, 1/10, 1/20, 1/40, etc.
		lootChance *= Math.pow(1/2f, Dungeon.LimitedDrops.GUARD_ARM.count);
		super.rollToDropLoot();
	}

	@Override
	protected Item createLoot() {
		Dungeon.LimitedDrops.GUARD_ARM.count++;
		return super.createLoot();
	}

	private final String CHAINSUSED = "chainsused";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHAINSUSED, chainsUsed);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chainsUsed = bundle.getBoolean(CHAINSUSED);
	}
	
	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			
			if (!chainsUsed
					&& enemyInFOV
					&& !isCharmedBy( enemy )
					&& !canAttack( enemy )
					&& Dungeon.level.distance( pos, enemy.pos ) < 5
					&& Random.Int(3) == 0
					
					&& chain(enemy.pos)){
				return false;
			} else {
				return super.act( enemyInFOV, justAlerted );
			}
			
		}
	}
}