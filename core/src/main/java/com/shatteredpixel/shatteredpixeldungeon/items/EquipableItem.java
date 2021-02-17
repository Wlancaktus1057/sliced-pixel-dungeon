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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public abstract class EquipableItem extends Item {

	public static final String AC_EQUIP		= "EQUIP";
	public static final String AC_UNEQUIP	= "UNEQUIP";
	public static final String AC_CURSE	    = "CURSE";

	{
		bones = true;
	}

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( isEquipped( hero ) ? AC_UNEQUIP : AC_EQUIP );
		if (hero.heroClass == HeroClass.HERETIC
				&& (this instanceof MeleeWeapon || this instanceof Armor)
				&& !(this instanceof MissileWeapon)) {
			actions.add( AC_CURSE );
		}
		if (hero.heroClass == HeroClass.ELEMENTALIST
				&& this instanceof Armor) {
			actions.remove(AC_EQUIP);
			actions.remove(AC_UNEQUIP);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EQUIP )) {
			//In addition to equipping itself, item reassigns itself to the quickslot
			//This is a special case as the item is being removed from inventory, but is staying with the hero.
			int slot = Dungeon.quickslot.getSlot( this );
			doEquip(hero);
			if (slot != -1) {
				Dungeon.quickslot.setSlot( slot, this );
				updateQuickslot();
			}
		} else if (action.equals( AC_UNEQUIP )) {
			doUnequip( hero, true );

		} else if (action.equals( AC_CURSE )) {

			if (!isEquipped( hero )) {
				GLog.w(Messages.get(this, "heretic_need_equip"));
			} else if (!isIdentified()) {
				GLog.w(Messages.get(this, "heretic_fail_curse"));
			} else {

			if (this instanceof MeleeWeapon) {
				Weapon w = (Weapon) this;
				if (w.hasCurseEnchant()) {
					if (hero.pointsInTalent(Talent.ENHANCED_CURSE) == 2){
						w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
						GLog.p( Messages.get(this, "heretic_weapon_curse", w.name()));
						hero.spend( 1f );
						hero.busy();

						hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
						Sample.INSTANCE.play(Assets.Sounds.CURSED);
						Dungeon.hero.sprite.operate(Dungeon.hero.pos);

						updateQuickslot();
					} else GLog.w(Messages.get(this, "heretic_fail_curse"));

				} else {
					cursed = true;
					if (w.enchantment != null) {
					    w.enchant(Weapon.Enchantment.randomCurse(w.enchantment.getClass()));
					} else {
						w.enchant(Weapon.Enchantment.randomCurse());
				    }

					GLog.p( Messages.get(this, "heretic_weapon_curse", w.name()));
					hero.spend( 1f );
					hero.busy();

					hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
					Dungeon.hero.sprite.operate(Dungeon.hero.pos);

					updateQuickslot();
			    }
			} else if (this instanceof Armor){
				Armor a = (Armor) this;
				if (a.hasCurseGlyph()) {
					if (hero.pointsInTalent(Talent.ENHANCED_CURSE) == 2){
						a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
						GLog.p( Messages.get(this, "heretic_armor_curse", a.name()));
						hero.spend( 1f );
						hero.busy();

						hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
						Sample.INSTANCE.play(Assets.Sounds.CURSED);
						Dungeon.hero.sprite.operate(Dungeon.hero.pos);

						updateQuickslot();
					}
					else GLog.w(Messages.get(this, "heretic_fail_curse"));

				} else {
					cursed = true;
					if (a.glyph != null){
						a.inscribe(Armor.Glyph.randomCurse(a.glyph.getClass()));
					} else {
						a.inscribe(Armor.Glyph.randomCurse());
					}

					GLog.p( Messages.get(this, "heretic_armor_curse", a.name()));
					hero.spend( 1f );
					hero.busy();

					hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
					Dungeon.hero.sprite.operate(Dungeon.hero.pos);

					updateQuickslot();
				}
			} else {
				cursed = true;

				GLog.p( Messages.get(this, "heretic_other_curse", this.name()));
				hero.spend(1f);
				hero.busy();

				hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
				Sample.INSTANCE.play( Assets.Sounds.CURSED );
				Dungeon.hero.sprite.operate(Dungeon.hero.pos);

				updateQuickslot();
			} }
		}
	}

	@Override
	public void doDrop( Hero hero ) {
		if (!isEquipped( hero ) || doUnequip( hero, false, false )) {
			super.doDrop( hero );
		}
	}

	@Override
	public void cast( final Hero user, int dst ) {

		if (isEquipped( user )) {
			if (quantity == 1 && !this.doUnequip( user, false, false )) {
				return;
			}
		}

		super.cast( user, dst );
	}

	public static void equipCursed( Hero hero ) {
		hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
		Sample.INSTANCE.play( Assets.Sounds.CURSED );
	}

	protected float time2equip( Hero hero ) {
		if (this instanceof MeleeWeapon && hero.pointsInTalent(Talent.INDUSTRIOUS_HANDS) == 2) {
			return 0;
		}
		return 1;
	}

	public abstract boolean doEquip( Hero hero );

	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {

		if (cursed && hero.buff(MagicImmune.class) == null) {
			GLog.w(Messages.get(EquipableItem.class, "unequip_cursed"));
			return false;
		}

		if (single) {
			hero.spendAndNext( time2equip( hero ) );
		} else {
			hero.spend( time2equip( hero ) );
		}

		if (!collect || !collect( hero.belongings.backpack )) {
			onDetach();
			Dungeon.quickslot.clearItem(this);
			updateQuickslot();
			if (collect) Dungeon.level.drop( this, hero.pos );
		}

		return true;
	}

	final public boolean doUnequip( Hero hero, boolean collect ) {
		return doUnequip( hero, collect, true );
	}

	public void activate( Char ch ){}
}
