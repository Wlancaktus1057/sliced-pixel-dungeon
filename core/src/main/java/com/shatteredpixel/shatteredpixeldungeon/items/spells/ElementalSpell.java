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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpellWeave;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.elementalist.Resonance;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ElementalSpell extends TargetedSpell {

	{
		unique = true;
		bones = false;
		levelKnown = true;
		usesTargeting = true;
		defaultAction = AC_CAST;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		//prevents exploits
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}

	@Override
	protected void fx( Ballistica bolt, Callback callback ) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.MAGIC_MISSILE,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	protected void affectTarget(Ballistica bolt, Hero hero) {
		int cell = bolt.collisionPos;
	}

	@Override
	public int value() { return 0; }

	@Override
	public boolean isUpgradable() { return true; }

	@Override
	public boolean isIdentified() { return true; }

	// focus
	public static class FireFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		private float left;
		private int ardent;
		private static final String LEFT 	= "left";
		private static final String ARDENT  = "ardent";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
			bundle.put( ARDENT, ardent );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
			ardent = bundle.getInt( ARDENT );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS)
							+ (curUser.subClass == HeroSubClass.BINDER ? 10 : 0);
		}
		public void ardent(int num) {
			ardent += num;
		}
		public int getArdent() { return ardent; }

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}

			spend( TICK );
			left -= TICK;
			if (left <= 0) {
				ardent = 0;
				detach();
				return true;
			}

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)) {

				if (target.buff(IceFocus.class) != null) {
					Sample.INSTANCE.play(Assets.Sounds.GAS);
					CellEmitter.get(target.pos).burst(Speck.factory(Speck.STEAM), 6);
					Buff.detach(target, IceFocus.class);
					Buff.detach(target, FireFocus.class);
					Splash.at(target.pos, 0x00AAFF, 10);
					for (int i : PathFinder.NEIGHBOURS8) {
						if (Dungeon.hero.hasTalent(Talent.HYDROMANCER)) {
							Dungeon.level.setCellToWater(false, target.pos + i);

							if (Dungeon.hero.pointsInTalent(Talent.HYDROMANCER) == 2) {
								CellEmitter.center(target.pos).burst(Speck.factory(Speck.STEAM_BLAST), 2);
								Char ch = Actor.findChar(target.pos+i);
								if (ch != null) {
									Ballistica trajectory = new Ballistica(target.pos, ch.pos, Ballistica.STOP_TARGET);
									trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
									WandOfBlastWave.throwChar(ch, trajectory, 2, true, true);
								}
							}

						} else Dungeon.level.setCellToWater(false, target.pos);
					}
				} else {
					if (Dungeon.level != null && Dungeon.level.map[target.pos] == Terrain.WATER){
						Level.set( target.pos, Terrain.EMPTY );
						GameScene.updateMap( target.pos );
						CellEmitter.get( target.pos ).burst( Speck.factory( Speck.STEAM ), 10 );
					}
				}
				return true;
			} else
				return false;
		}

		@Override public int icon() {
			if (((Hero)target).subClass == HeroSubClass.BINDER)
				return BuffIndicator.SC_FIRE_BIND;
			return BuffIndicator.SC_FIRE;
		}
		@Override public float iconFadePercent() {
			if (target instanceof Hero){
				float max = DURATION
						+ ((Hero) target).pointsInTalent(Talent.EXTENDED_FOCUS)
						+ (((Hero) target).subClass == HeroSubClass.BINDER ? 10f : 0f);
				return Math.max(0, (max-left)/max);
			}
			return 0;
		}
		@Override public String toString() {
			return Messages.get(this, "name");
		}
		@Override public String desc() {
			String desc = Messages.get(this, "desc", left, ElementalSpellFire.min(), ElementalSpellFire.max() );

			if (((Hero)target).subClass == HeroSubClass.BINDER){
				ElementalSpell fire = curUser.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
				int lvl = fire.buffedLvl();
				int tier = Math.max(1, Math.round(fire.buffedLvl() * 0.5f));

				desc += "\n\n" + Messages.get(this, "desc_bind",tier+lvl, 5*(tier+1)+lvl*(tier+1));
			}
			return desc;
		}

	};

	public static class IceFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		private float left;
		private static final String LEFT = "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS)
					+ (curUser.subClass == HeroSubClass.BINDER ? 10 : 0);
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}
			spend( TICK );
			left -= TICK;
			if (left <= 0) {
				detach();
				return true;
			}

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override
		public boolean attachTo(Char target) {
			if (super.attachTo(target)){

				if (target.buff(FireFocus.class) != null) {

					Sample.INSTANCE.play(Assets.Sounds.GAS);
					CellEmitter.get(target.pos).burst(Speck.factory(Speck.STEAM), 6);
					Buff.detach(target, IceFocus.class);
					Buff.detach(target, FireFocus.class);

					Splash.at(target.pos, 0x00AAFF, 10);
					for (int i : PathFinder.NEIGHBOURS8) {
						if (Dungeon.hero.hasTalent(Talent.HYDROMANCER)) {
							Dungeon.level.setCellToWater(false, target.pos + i);

							if (Dungeon.hero.pointsInTalent(Talent.HYDROMANCER) == 2) {
								CellEmitter.center(target.pos).burst(Speck.factory(Speck.STEAM_BLAST), 2);
								Char ch = Actor.findChar(target.pos+i);
								if (ch != null) {
									Ballistica trajectory = new Ballistica(target.pos, ch.pos, Ballistica.STOP_TARGET);
									trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size()-1), Ballistica.PROJECTILE);
									WandOfBlastWave.throwChar(ch, trajectory, 2, true, true);
								}
							}

						} else Dungeon.level.setCellToWater(false, target.pos);
					}
				}
				return true;
			} else
				return false;
		}

		@Override public int icon() {
			if (((Hero)target).subClass == HeroSubClass.BINDER)
				return BuffIndicator.SC_ICE_BIND;
			return BuffIndicator.SC_ICE;
		}
		@Override public float iconFadePercent() {
			if (target instanceof Hero){
				float max = DURATION
						+ ((Hero) target).pointsInTalent(Talent.EXTENDED_FOCUS)
						+ (((Hero) target).subClass == HeroSubClass.BINDER ? 10f : 0f);
				return Math.max(0, (max-left)/max);
			}
			return 0;
		}
		@Override public String toString() {
			return Messages.get(this, "name");
		}
		@Override public String desc() {
			String desc = Messages.get(this, "desc", left, ElementalSpellIce.min(), ElementalSpellIce.max() );

			if (((Hero)target).subClass == HeroSubClass.BINDER){
				desc += "\n\n" + Messages.get(this, "desc_bind", DRMin(), DRMax());
			}
			return desc;
		}

		public int DRMax(){
			ElementalSpell ice = curUser.belongings.getItem(ElementalSpell.ElementalSpellIce.class);
			int lvl = ice.buffedLvl();
			int tier = Math.max(1, Math.round(ice.buffedLvl() * 0.5f));

			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 1 + tier + lvl;
			}

			int max = tier * (2 + lvl);

			if (lvl > max){
				return ((lvl - max)+1)/2;
			} else {
				return max;
			}
		}

		public int DRMin(){
			if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
				return 0;
			}

			int max = DRMax();

			ElementalSpell ice = curUser.belongings.getItem(ElementalSpell.ElementalSpellIce.class);
			int lvl = ice.buffedLvl();
			int tier = Math.max(1, Math.round(ice.buffedLvl() * 0.5f));

			if (lvl >= max){
				return (lvl - max);
			} else {
				return lvl;
			}
		}
	};

	public static class ElecFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		private float left;
		private static final String LEFT = "left";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			Hero hero = Dungeon.hero;
			left = duration + (float)hero.pointsInTalent(Talent.EXTENDED_FOCUS)
					+ (hero.subClass == HeroSubClass.BINDER ? 10 : 0);
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}
			spend( TICK );
			left -= TICK;
			if (Dungeon.level.water[target.pos] && !target.flying) {
				target.sprite.centerEmitter().burst(EnergyParticle.BURST, 10);
				detach();
				return true;
			}

			if (left <= 0) detach();

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override public int icon() {
			if (((Hero)target).subClass == HeroSubClass.BINDER)
				return BuffIndicator.SC_ELEC_BIND;
			return BuffIndicator.SC_ELEC;
		}
		@Override public float iconFadePercent() {
			if (target instanceof Hero){
				float max = DURATION
						 	+ ((Hero) target).pointsInTalent(Talent.EXTENDED_FOCUS)
							+ (((Hero) target).subClass == HeroSubClass.BINDER ? 10f : 0f);
				return Math.max(0, (max-left)/max);
			}
			return 0;
		}
		@Override public String toString() { return Messages.get(this, "name"); }
		@Override public String desc() {
			String desc = Messages.get(this, "desc", left, ElementalSpellElec.min(), ElementalSpellElec.max() );

			if (((Hero)target).subClass == HeroSubClass.BINDER){
				ElementalSpell elec = curUser.belongings.getItem(ElementalSpell.ElementalSpellElec.class);
				int lvl = elec.buffedLvl();
				int tier = Math.max(1, Math.round(elec.buffedLvl() * 0.5f));

				desc += "\n\n" + Messages.get(this, "desc_bind",
						2*tier+(tier == 1 ? lvl : 2*lvl), 5*tier+(tier == 1 ? 2*lvl : tier*lvl));
			}
			return desc;
		}
	};

	public static class ChaosFocus extends Buff {

		public static final float DURATION = 3f;

		{
			type = buffType.POSITIVE;
		}

		public enum FocusType {
			FIRE, ICE, ELEC, NORMAL
		}
		public FocusType state = FocusType.NORMAL;

		private float left;
		private static final String LEFT = "left";
		private static final String FOCUSTYPE = "state";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEFT, left );
			bundle.put( FOCUSTYPE, state );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle(bundle);
			left = bundle.getFloat( LEFT );
			state = bundle.getEnum( FOCUSTYPE, FocusType.class );
		}

		public void set( Char ch ) {
			set( ch, DURATION );
		}
		public void set( Char ch, float duration ) {
			left = duration + (float)Dungeon.hero.pointsInTalent(Talent.EXTENDED_FOCUS)
					+ (curUser.subClass == HeroSubClass.BINDER ? 10 : 0);
		}
		public void check( ElementalSpell element ) {
			left += 2f;
			if (element instanceof ElementalSpellFire) state = FocusType.FIRE;
			if (element instanceof ElementalSpellIce) state = FocusType.ICE;
			if (element instanceof ElementalSpellElec) state = FocusType.ELEC;
			BuffIndicator.refreshHero();
		}

		@Override
		public boolean act() {
			if (!target.isAlive()) {
				detach();
			}
			spend( TICK );
			left -= TICK;

			if (left <= 0) detach();

			if (((Hero)target).hasTalent(Talent.ELEMENTAL_SHIELD)) {
				Barrier barrier = target.buff(Barrier.class);
				if (barrier != null) {
					if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.ELEMENTAL_SHIELD)) {
						barrier.incShield(1);
					}
				} else Buff.affect(target, Barrier.class).setShield(1);
			}

			return true;
		}

		@Override public int icon() {
			if (state == FocusType.FIRE) return BuffIndicator.SC_FIRE;
			if (state == FocusType.ICE)  return BuffIndicator.SC_ICE;
			if (state == FocusType.ELEC) return BuffIndicator.SC_ELEC;
			else return BuffIndicator.SC_CHAOS;
		}
		@Override public float iconFadePercent() {
			if (target instanceof Hero){
				float max = DURATION
						+ ((Hero) target).pointsInTalent(Talent.EXTENDED_FOCUS)
						+ (((Hero) target).subClass == HeroSubClass.BINDER ? 10f : 0f);
				return Math.max(0, (max-left)/max);
			}
			return 0;
		}
		@Override public String toString() { return Messages.get(this, "name"); }
		@Override public String desc() {
			String desc = Messages.get(this, "desc", left);

			if (state == FocusType.FIRE){
				desc = Messages.get(FireFocus.class, "desc", left,
						ElementalSpellFire.min(), ElementalSpellFire.max() );
			} else if (state == FocusType.ICE){
				desc = Messages.get(IceFocus.class, "desc", left,
						ElementalSpellIce.min(), ElementalSpellIce.max() );
			} else if (state == FocusType.ELEC){
				desc = Messages.get(ElecFocus.class, "desc", left,
						ElementalSpellElec.min(), ElementalSpellElec.max() );
			}

			return desc;
		}
	};

	// spell
	public static class ElementalSpellFire extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_FIRE;
		}

		@Override
		public int buffedLvl(){
			Hero hero = Dungeon.hero;
			FireFocus focus = hero.buff(FireFocus.class);
			if (focus != null && focus.getArdent() > 1
					&& hero.pointsInTalent(Talent.ARDENT_BLADE) == 3){
				return super.buffedLvl() + focus.getArdent();
			}

			return super.buffedLvl();
		}

		public static int min() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellFire.class);
			int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
			if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
			return 2+(int)(1.5f*item.buffedLvl())+(2*bonus);
		}
		public static int max() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellFire.class);
			int bonus = 1 + (hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
			if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
			return 4+(int)(2.5f*item.buffedLvl())+(2+(2*bonus));
		}

		public static int damageRoll() { return Random.NormalIntRange( min(), max() ); }

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(FireFocus.class) != null) {
				super.onCast(hero);
			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.emitter().burst(FlameParticle.FACTORY, 15);

				Buff.affect(hero, FireFocus.class).set(hero, FireFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();

				SpellWeave weave = hero.buff(SpellWeave.class);
				if (weave != null && weave.checkBendTime()){
					Invisibility.dispel_bendTime();
				} else {
					Invisibility.dispel();
				}

				updateQuickslot();
				curUser.spendAndNext(Actor.TICK);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FIRE,
					curUser.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			Buff.detach(hero, FireFocus.class);
			int cell = bolt.collisionPos;
			Splash.at(cell, 0xFFFFBB33, 10);
			Char target = Actor.findChar(cell);
			if (hero.buff(ChaosFocus.class) != null) {
				hero.buff((ChaosFocus.class)).check(this);
			}

			if (target != null) {
					Buff.affect(target, Burning.class).reignite(target);
					target.damage(damageRoll(), this);

					if (curUser.subClass == HeroSubClass.SPELLWEAVER){
						Buff.affect(curUser, SpellWeave.class).gainCount();
						SpellWeave weave = hero.buff(SpellWeave.class);
						weave.addCount(1);
					}

					if (!target.isAlive() && Dungeon.hero.hasTalent(Talent.WILDFIRE)
							&& Random.Float() < 0.34f + 0.33f* Dungeon.hero.pointsInTalent(Talent.WILDFIRE)) {
						float extend = 3f + Dungeon.hero.pointsInTalent(Talent.WILDFIRE);
						Buff.affect(Dungeon.hero, ElementalSpell.FireFocus.class).set(Dungeon.hero, extend);
					}

			} else GameScene.add(Blob.seed(cell, 2, Fire.class));

			Resonance.ResonaceTracker resonanceTracker = hero.buff(Resonance.ResonaceTracker.class);
			if (resonanceTracker != null && resonanceTracker.count() > 0){
				Resonance.castResonace(this, hero);
				resonanceTracker.countDown(1);
			}
		}
		@Override public String desc() {
			return Messages.get(this, "desc", ElementalSpellFire.min(), ElementalSpellFire.max());
		}
	}

	public static class ElementalSpellIce extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_ICE;
		}

		public static int min() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellIce.class);
			int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
			if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
			return 1+(int)(0.5f*item.buffedLvl())+(2*bonus);
		}
		public static int max() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellIce.class);
			int bonus = 1 + (hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
			if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
			return 2+(int)(1.5f*item.buffedLvl())+(2+(2*bonus));
		}
		public static int damageRoll() { return Random.NormalIntRange( min(), max() ); }

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(IceFocus.class) != null) {
				super.onCast(hero);

			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.emitter().start(MagicMissile.MagicParticle.ATTRACTING, 0.02f, 20);

				Buff.affect(hero, IceFocus.class).set(hero, IceFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();

				SpellWeave weave = hero.buff(SpellWeave.class);
				if (weave != null && weave.checkBendTime()){
					Invisibility.dispel_bendTime();
				} else {
					Invisibility.dispel();
				}

				updateQuickslot();
				curUser.spendAndNext(Actor.TICK);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FROST,
					curUser.sprite,
					bolt.collisionPos,
					callback);
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			Buff.detach(hero, IceFocus.class);
			int cell = bolt.collisionPos;
			Splash.at(cell, 0xFF8EE3FF, 10);
			if (hero.buff(ChaosFocus.class) != null) {
				hero.buff((ChaosFocus.class)).check(this);
			}

			Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
			if (heap != null) {
				heap.freeze();
			}
			Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
			if (fire != null) {
				fire.clear( cell );
			}
			if (Dungeon.level.water[cell] && Blob.volumeAt(cell, Freezing.class) == 0) {
				GameScene.add( Blob.seed( cell, 2, Freezing.class ) );
			}

			Char target = Actor.findChar(cell);
			if (target != null) {
				if (target.buff(Frost.class) != null) {
					Buff.affect(target, Frost.class, 2f);
				} else {
					Chill chill = target.buff(Chill.class);
					if (target == hero && hero.hasTalent(Talent.ICEMAIL)) {
						// do not damage herself
					} else target.damage(damageRoll(), this);

					if (curUser.subClass == HeroSubClass.SPELLWEAVER){
						Buff.affect(curUser, SpellWeave.class).gainCount();
						SpellWeave weave = hero.buff(SpellWeave.class);
						weave.addCount(1);
					}

					Buff.affect(target, Chill.class, 3f);

					if (chill != null && chill.cooldown() >= 5f) {
						Buff.affect(target, Frost.class, Frost.DURATION);
					}
				}
			}

			Resonance.ResonaceTracker resonanceTracker = hero.buff(Resonance.ResonaceTracker.class);
			if (resonanceTracker != null && resonanceTracker.count() > 0){
				Resonance.castResonace(this, hero);
				resonanceTracker.countDown(1);
			}
		}

		@Override public String desc() {
			return Messages.get(this, "desc", ElementalSpellIce.min(), ElementalSpellIce.max());
		}
	}

	public static class ElementalSpellElec extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_ELEC;
		}

		public static int min() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellElec.class);
			int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
			if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
			return 3+(int)(1.5f*item.buffedLvl())+(2*bonus);
		}
		public static int max() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellElec.class);
			int bonus = 1+(hero.pointsInTalent(Talent.ELEMENTAL_MASTER));
			if (!hero.hasTalent(Talent.ELEMENTAL_MASTER)) bonus = 0;
			return 8+(3*item.buffedLvl())+(2+(2*bonus));
		}
		public static int damageRoll() { return Random.NormalIntRange( min(), max() ); }

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(ElecFocus.class) != null) {
				super.onCast(hero);

			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);

				Buff.affect(hero, ElecFocus.class).set(hero, ElecFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();

				SpellWeave weave = hero.buff(SpellWeave.class);
				if (weave != null && weave.checkBendTime()){
					Invisibility.dispel_bendTime();
				} else {
					Invisibility.dispel();
				}

				updateQuickslot();
				curUser.spendAndNext(Actor.TICK);
			}
		}

		private ArrayList<Char> affected = new ArrayList<>();
		private ArrayList<Lightning.Arc> arcs = new ArrayList<>();

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			Buff.detach(curUser, ElecFocus.class);

			if (hero.buff(ChaosFocus.class) != null) {
				hero.buff((ChaosFocus.class)).check(this);
			}

			//lightning deals less damage per-target, the more targets that are hit.
			float multipler = 0.4f + (0.6f/affected.size());
			//if the main target is in water, all affected take full damage
			if (Dungeon.level.water[bolt.collisionPos]) multipler = 1f;
			if (Dungeon.hero.hasTalent(Talent.CHAIN_LIGHTNING)) {
				multipler = 1f;
				if (Dungeon.hero.pointsInTalent(Talent.CHAIN_LIGHTNING) == 2)
					multipler += (multipler*0.1f)*affected.size();
			}

			for (Char ch : affected){

				if (ch == hero) Camera.main.shake(2, 0.3f);
				ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				ch.sprite.flash();
				if (ch != curUser &&
						ch.alignment == curUser.alignment
						&& ch.pos != bolt.collisionPos) {
					continue;
				}
				ch.damage((int) (damageRoll() * multipler), this);

				if (curUser.subClass == HeroSubClass.SPELLWEAVER){
					Buff.affect(curUser, SpellWeave.class).gainCount();
					SpellWeave weave = hero.buff(SpellWeave.class);
					weave.addCount(1);
				}

				Resonance.ResonaceTracker resonanceTracker = hero.buff(Resonance.ResonaceTracker.class);
				if (resonanceTracker != null && resonanceTracker.count() > 0){
					Resonance.castResonace(this, hero);
					resonanceTracker.countDown(1);
				}
			}
		}

		private void arc( Char ch ) {
			int dist = (Dungeon.level.water[ch.pos] && !ch.flying) ? 2 : 1;
			ArrayList<Char> hitThisArc = new ArrayList<>();
			PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE){
					Char n = Actor.findChar( i );
					if (n == Dungeon.hero && PathFinder.distance[i] > 1)
						//the hero is only zapped if they are adjacent
						continue;
					else if (n != null && !affected.contains( n )) {
						hitThisArc.add(n);
					}
				}
			}

			affected.addAll(hitThisArc);
			for (Char hit : hitThisArc){
				arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
				arc(hit);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			affected.clear();
			arcs.clear();
			int cell = bolt.collisionPos;
			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affected.add( ch );
				arcs.add( new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
				arc(ch);
			} else {
				arcs.add( new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
				CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
			}
			curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
			callback.call();
		}

		@Override public String desc() {
			return Messages.get(this, "desc", ElementalSpellElec.min(), ElementalSpellElec.max());
		}
	}

	public static class ElementalSpellChaos extends ElementalSpell {

		{
			image = ItemSpriteSheet.ELEMENT_CHAOS;
		}

		@Override
		public boolean isUpgradable() { return false; }

		@Override
		public int buffedLvl() {
			Hero hero = Dungeon.hero;
			Item item = hero.belongings.getItem(ElementalSpellChaos.class);
			ChaosFocus focus = hero.buff(ChaosFocus.class);
			if (hero.pointsInTalent(Talent.ELEMENT_OF_CHAOS) == 2 && focus != null) {
				switch (focus.state) {
					case NORMAL:
					default:
						return 0;
					case FIRE:
						return hero.belongings.getItem(ElementalSpellFire.class).buffedLvl();
					case ICE:
						return hero.belongings.getItem(ElementalSpellIce.class).buffedLvl();
					case ELEC:
						return hero.belongings.getItem(ElementalSpellElec.class).buffedLvl();
				}
			}
			if (hero.pointsInTalent(Talent.ELEMENT_OF_CHAOS) == 3) {
				int firelvl = hero.belongings.getItem(ElementalSpellFire.class).buffedLvl();
				int icelvl  = hero.belongings.getItem(ElementalSpellIce.class).buffedLvl();
				int eleclvl = hero.belongings.getItem(ElementalSpellElec.class).buffedLvl();
				return Math.max(Math.max(Math.max(firelvl, icelvl), Math.max(icelvl, eleclvl)),
								Math.max(Math.max(firelvl, icelvl), Math.max(firelvl, eleclvl)));
			}
			return 0;
		}

		@Override
		protected void onCast(Hero hero) {
			if (hero.buff(ChaosFocus.class) != null) {
				super.onCast(hero);
			} else {
				curUser.sprite.zap(curUser.pos);
				curUser.sprite.emitter().start( RainbowParticle.ATTRACTING, 0.02f, 20 );

				Buff.affect(hero, ChaosFocus.class).set(hero, ChaosFocus.DURATION);
				Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				curUser.busy();

				SpellWeave weave = hero.buff(SpellWeave.class);
				if (weave != null && weave.checkBendTime()){
					Invisibility.dispel_bendTime();
				} else {
					Invisibility.dispel();
				}

				updateQuickslot();
				curUser.spendAndNext(Actor.TICK);
			}
		}

		private ArrayList<Char> affected = new ArrayList<>();
		private ArrayList<Lightning.Arc> arcs = new ArrayList<>();
		private void arc( Char ch ) {
			int dist = (Dungeon.level.water[ch.pos] && !ch.flying) ? 2 : 1;
			ArrayList<Char> hitThisArc = new ArrayList<>();
			PathFinder.buildDistanceMap( ch.pos, BArray.not( Dungeon.level.solid, null ), dist );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] < Integer.MAX_VALUE){
					Char n = Actor.findChar( i );
					if (n == Dungeon.hero && PathFinder.distance[i] > 1)
						//the hero is only zapped if they are adjacent
						continue;
					else if (n != null && !affected.contains( n )) {
						hitThisArc.add(n);
					}
				}
			}

			affected.addAll(hitThisArc);
			for (Char hit : hitThisArc){
				arcs.add(new Lightning.Arc(ch.sprite.center(), hit.sprite.center()));
				arc(hit);
			}
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			ChaosFocus focus = Dungeon.hero.buff(ChaosFocus.class);
			if (focus != null) {
				if (focus.state == ChaosFocus.FocusType.FIRE) {
					MagicMissile.boltFromChar( curUser.sprite.parent,
							MagicMissile.FIRE,
							curUser.sprite,
							bolt.collisionPos,
							callback);
					Sample.INSTANCE.play( Assets.Sounds.ZAP );
				}
				else if (focus.state == ChaosFocus.FocusType.ICE) {
					MagicMissile.boltFromChar( curUser.sprite.parent,
							MagicMissile.FROST,
							curUser.sprite,
							bolt.collisionPos,
							callback);
					Sample.INSTANCE.play( Assets.Sounds.ZAP );
				}
				else if (focus.state == ChaosFocus.FocusType.ELEC) {
					affected.clear();
					arcs.clear();
					int cell = bolt.collisionPos;
					Char ch = Actor.findChar( cell );
					if (ch != null) {
						affected.add( ch );
						arcs.add( new Lightning.Arc(curUser.sprite.center(), ch.sprite.center()));
						arc(ch);
					} else {
						arcs.add( new Lightning.Arc(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(bolt.collisionPos)));
						CellEmitter.center( cell ).burst( SparkParticle.FACTORY, 3 );
					}
					curUser.sprite.parent.addToFront( new Lightning( arcs, null ) );
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
					callback.call();
				}
				else {
					MagicMissile.boltFromChar(curUser.sprite.parent,
							MagicMissile.RAINBOW,
							curUser.sprite,
							bolt.collisionPos,
							callback);
					Sample.INSTANCE.play(Assets.Sounds.ZAP);
				}
			}
		}

		@Override
		protected void affectTarget(Ballistica bolt, Hero hero) {
			ChaosFocus focus = Dungeon.hero.buff(ChaosFocus.class);
			if (focus != null) {
				int cell = bolt.collisionPos;
				Char target = Actor.findChar(cell);
				if (focus.state == ChaosFocus.FocusType.FIRE) {
					Splash.at(cell, 0xFFFFBB33, 10);

					if (target != null) {
						Buff.affect(target, Burning.class).reignite(target);
						target.damage(ElementalSpellFire.damageRoll(), this);

						if (curUser.subClass == HeroSubClass.SPELLWEAVER){
							Buff.affect(curUser, SpellWeave.class).gainCount();
							SpellWeave weave = hero.buff(SpellWeave.class);
							weave.addCount(1);
						}

						if (!target.isAlive() && Dungeon.hero.hasTalent(Talent.WILDFIRE)
								&& Random.Float() < 0.34f + 0.33f* Dungeon.hero.pointsInTalent(Talent.WILDFIRE)) {
							float extend = 3f + Dungeon.hero.pointsInTalent(Talent.WILDFIRE);
							Buff.affect(Dungeon.hero, ElementalSpell.FireFocus.class).set(Dungeon.hero, extend);
						}
					} else GameScene.add(Blob.seed(cell, 2, Fire.class));
				}
				else if (focus.state == ChaosFocus.FocusType.ICE) {
					Splash.at(cell, 0xFF8EE3FF, 10);
					Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
					if (heap != null) {
						heap.freeze();
					}
					Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
					if (fire != null) {
						fire.clear( cell );
					}
					if (Dungeon.level.water[cell] && Blob.volumeAt(cell, Freezing.class) == 0) {
						GameScene.add( Blob.seed( cell, 2, Freezing.class ) );
					}

					if (target != null) {

						if (target.buff(Frost.class) != null) {
							Buff.affect(target, Frost.class, 2f);
						} else {
							Chill chill = target.buff(Chill.class);
							if (target == hero && hero.hasTalent(Talent.ICEMAIL)) {
								// do not damage herself
							} else target.damage(ElementalSpellIce.damageRoll(), this);
							Buff.affect(target, Chill.class, 3f);

							if (curUser.subClass == HeroSubClass.SPELLWEAVER){
								Buff.affect(curUser, SpellWeave.class).gainCount();
								SpellWeave weave = hero.buff(SpellWeave.class);
								weave.addCount(1);
							}

							if (chill != null && chill.cooldown() >= 5f) {
								Buff.affect(target, Frost.class, Frost.DURATION);
							}
						}
					}
				}
				else if (focus.state == ChaosFocus.FocusType.ELEC) {
					//lightning deals less damage per-target, the more targets that are hit.
					float multipler = 0.4f + (0.6f/affected.size());
					//if the main target is in water, all affected take full damage
					if (Dungeon.level.water[bolt.collisionPos]) multipler = 1f;
					if (Dungeon.hero.hasTalent(Talent.CHAIN_LIGHTNING)) {
						multipler = 1f;
						if (Dungeon.hero.pointsInTalent(Talent.CHAIN_LIGHTNING) == 2)
							multipler += (multipler*0.1f)*affected.size();
					}

					for (Char ch : affected){
						if (ch == hero) Camera.main.shake(2, 0.3f);
						ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
						ch.sprite.flash();
						if (ch != curUser &&
								ch.alignment == curUser.alignment
								&& ch.pos != bolt.collisionPos) {
							continue;
						}
						ch.damage((int) (ElementalSpellElec.damageRoll() * multipler), this);

						if (curUser.subClass == HeroSubClass.SPELLWEAVER){
							Buff.affect(curUser, SpellWeave.class).gainCount();
							SpellWeave weave = hero.buff(SpellWeave.class);
							weave.addCount(1);
						}
					}
					Resonance.ResonaceTracker resonanceTracker = hero.buff(Resonance.ResonaceTracker.class);
					if (resonanceTracker != null && resonanceTracker.count() > 0){
						Resonance.castResonace(this, hero);
						resonanceTracker.countDown(1);
					}
					Buff.detach(hero, ChaosFocus.class);
				}

				else {

					Buff.detach(hero, ChaosFocus.class);
					CellEmitter.center(cell).burst(RainbowParticle.BURST, 20);

					Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
					Freezing freezing = (Freezing) Dungeon.level.blobs.get(Freezing.class);
					Electricity electricity = (Electricity) Dungeon.level.blobs.get(Electricity.class);
					// elemental blobs
					if (fire != null && fire.volume > 0) {
						int vol = fire.volume;
						fire.clear(cell);
						GameScene.add(Blob.seed(cell, vol, Freezing.class));

					} else if (freezing != null && freezing.volume > 0) {
						int vol = freezing.volume;
						freezing.clear(cell);
						GameScene.add(Blob.seed(cell, vol, Fire.class));

					} else if (electricity != null && electricity.volume > 0) {
						int vol = electricity.cur[cell];
						electricity.clear(cell);
						Char elec = Actor.findChar(cell);
						if (elec != null) {

							elec.damage(Random.NormalIntRange((int) (vol * 1.5f), vol * 2), Electricity.class);
							if (curUser.subClass == HeroSubClass.SPELLWEAVER){
								Buff.affect(curUser, SpellWeave.class).gainCount();
								SpellWeave weave = hero.buff(SpellWeave.class);
								weave.addCount(1);
							}

							CharSprite s = elec.sprite;
							if (s != null && s.parent != null) {
								ArrayList<Lightning.Arc> arcs = new ArrayList<>();
								arcs.add(new Lightning.Arc(new PointF(s.x, s.y + s.height / 2),
										new PointF(s.x + s.width, s.y + s.height / 2)));
								arcs.add(new Lightning.Arc(new PointF(s.x + s.width / 2, s.y),
										new PointF(s.x + s.width / 2, s.y + s.height)));
								s.parent.add(new Lightning(arcs, null));
								Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
							}
						} else Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
					}

					// damm webs!
					Web web = (Web) Dungeon.level.blobs.get(Web.class);
					if (web != null && web.volume > 0) {
						web.clear(cell);
					}

					// grass and water
					int t = Dungeon.level.map[cell];
					if (t == Terrain.WATER) {
						Level.set(cell, Terrain.EMPTY);
						GameScene.updateMap(cell);
					} else if (t == Terrain.EMPTY && t != Terrain.EMBERS) {
						Level.set(cell, Terrain.GRASS);
						GameScene.updateMap(cell);
					} else if (t == Terrain.EMBERS) {
						Level.set(cell, Terrain.EMPTY);
						GameScene.updateMap(cell);
						if (Blob.volumeAt(cell, Fire.class) == 0)
							GameScene.add(Blob.seed(cell, 5, Fire.class));
					} else if (t == Terrain.GRASS) {
						Level.set(cell, Terrain.FURROWED_GRASS);
						GameScene.updateMap(cell);
					} else if (t == Terrain.HIGH_GRASS || t == Terrain.FURROWED_GRASS) {
						Level.set(cell, Terrain.WATER);
						GameScene.updateMap(cell);
					}

					// doors
					int d = Dungeon.level.map[cell];
					if (d == Terrain.DOOR) {
						Level.set(cell, Terrain.OPEN_DOOR);
						GameScene.updateMap(cell);
					} else if (d == Terrain.OPEN_DOOR) {
						Level.set(cell, Terrain.DOOR);
						GameScene.updateMap(cell);
					}
				}
			}
		}
	}

	// binder
	public static class BinderFire extends MeleeWeapon {

		{
			image = ItemSpriteSheet.WEAPON_HOLDER;
			hitSound = Assets.Sounds.BURNING;
			hitSoundPitch = 1.2f;

			tier = 1;
		}

		@Override
		public int min(int lvl) {
			Hero hero = Dungeon.hero;
			ElementalSpell fire = hero.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
			lvl = fire.buffedLvl();
			int tier = Math.max(1, Math.round(fire.buffedLvl() * 0.5f));

			return  tier +  //base
					lvl;    //level scaling
		}

		@Override
		public int max(int lvl) {
			Hero hero = Dungeon.hero;
			ElementalSpell fire = hero.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
			lvl = fire.buffedLvl();
			int tier = Math.max(1, Math.round(fire.buffedLvl() * 0.5f));

			return  5*(tier+1) +    //base
					lvl*(tier+1);   //level scaling
		}

		@Override
		public int STRReq(int lvl){
			Hero hero = Dungeon.hero;
			ElementalSpell fire = hero.belongings.getItem(ElementalSpell.ElementalSpellFire.class);
			lvl = fire.buffedLvl();
			int tier = Math.max(1, Math.round(fire.buffedLvl() * 0.5f));

			return STRReq(tier, lvl) - 2; //2 less str than normal for their tier
		}

		@Override
		public int proc( Char attacker, Char defender, int damage ) {
			Hero hero = Dungeon.hero;
			Buff.affect(defender, Burning.class).reignite(defender);
			if (hero.pointsInTalent(Talent.ARDENT_BLADE) >= 2
					&& defender.buff(Burning.class) != null){
				defender.sprite.emitter().burst( ElmoParticle.FACTORY, 6 );
				damage *= 2;

				FireFocus focus = hero.buff(FireFocus.class);
				if (hero.pointsInTalent(Talent.ARDENT_BLADE) == 3 && focus != null){
					focus.ardent(1);
				}
			}
			return super.proc( attacker, defender, damage );
		}
	}

	//Armor with IceFocus: see Hero.defenseProc()

	public static class BinderElec extends MissileWeapon {
		{
			image = ItemSpriteSheet.BINDER_ELEC;
			hitSound = Assets.Sounds.LIGHTNING;
			hitSoundPitch = 1.2f;

			tier = 1;
			baseUses = 1;
		}

		@Override
		public int min(int lvl) {
			Hero hero = Dungeon.hero;
			ElementalSpell elec = hero.belongings.getItem(ElementalSpell.ElementalSpellElec.class);
			lvl = elec.buffedLvl();
			int tier = Math.max(1, Math.round(elec.buffedLvl() * 0.5f));

			return  2 * tier +                      //base
					(tier == 1 ? lvl : 2*lvl);      //level scaling
		}

		@Override
		public int max(int lvl) {
			Hero hero = Dungeon.hero;
			ElementalSpell elec = hero.belongings.getItem(ElementalSpell.ElementalSpellElec.class);
			lvl = elec.buffedLvl();
			int tier = Math.max(1, Math.round(elec.buffedLvl() * 0.5f));

			return  5 * tier +                      //base
					(tier == 1 ? 2*lvl : tier*lvl); //level scaling
		}

		@Override
		public int STRReq(int lvl){
			Hero hero = Dungeon.hero;
			ElementalSpell elec = hero.belongings.getItem(ElementalSpell.ElementalSpellElec.class);
			lvl = elec.buffedLvl();
			int tier = Math.max(1, Math.round(elec.buffedLvl() * 0.5f));

			return STRReq(tier, lvl) - 3; //3 less str than normal for their tier
		}

		@Override
		public int proc( Char attacker, Char defender, int damage ) {
			Hero hero = Dungeon.hero;
			if (defender != null && hero.hasTalent(Talent.MIGHTY_THUNDER)) {
				int cell = defender.pos;

				final Ballistica bolt = new Ballistica(curUser.pos, cell,
						Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

				int maxDist = 6;
				int dist = Math.min(bolt.dist, maxDist);

				final ConeAOE cone = new ConeAOE(bolt, 6, 60,
						Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

				//cast to cells at the tip, rather than all cells, better performance.
				for (Ballistica ray : cone.outerRays){
					((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
							MagicMissile.SPARK_CONE,
							curUser.sprite,
							ray.path.get(ray.dist),
							null
					);
				}

				MagicMissile.boltFromChar(curUser.sprite.parent,
				MagicMissile.SPARK_CONE,
				curUser.sprite,
				bolt.path.get(dist / 2),
				new Callback() {
					@Override
					public void call() {
						for (int cell : cone.cells){
							if (cell == bolt.sourcePos){
								continue;
							}

							int count = 0;
							Char ch = Actor.findChar( cell );
							if (ch != null) {
								int dmg = Random.NormalIntRange(min(), max());
								dmg *= hero.pointsInTalent(Talent.MIGHTY_THUNDER) >= 2 ? 0.4f : 0.6f;

								if (ch != defender) {
									ch.damage((int)dmg, BinderElec.class);
									count++;
								}
							}
							if (count >= 2 && curUser.pointsInTalent(Talent.MIGHTY_THUNDER) == 3){
								Buff.affect(defender, Paralysis.class, 2f);
								if (ch != null && ch != defender)
									Buff.affect(ch, Paralysis.class, 2f);
							}
						}
					}
				});
			}

			return super.proc( attacker, defender, damage );
		}
	}

}