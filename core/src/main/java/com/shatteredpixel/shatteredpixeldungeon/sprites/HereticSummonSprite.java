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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.HereticSummon;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BloodParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public abstract class HereticSummonSprite extends MobSprite {

	protected int boltType;

	protected abstract int texOffset();

	private Emitter particles;
	protected abstract Emitter createEmitter();

	public HereticSummonSprite() {
		super();
		
		int c = texOffset();
		
		texture( Assets.Sprites.HERETIC_SUMMON );

		TextureFilm frames = new TextureFilm( texture, 24, 17 );
		
		idle = new Animation( 10, true );
		idle.frames( frames, c+0, c+0, c+0, c+0, c+0, c+0, c+0, c+1, c+1, c+1, c+1, c+0 );
		
		run = new Animation( 12, true );
		run.frames( frames, c+0, c+2, c+3, c+4, c+3, c+2 );
		
		attack = new Animation( 12, false );
		attack.frames( frames, c+5, c+6, c+7 );
		
		zap = attack.clone();
		
		die = new Animation( 15, false );
		die.frames( frames, c+0, c+8, c+9 );
		
		play( idle );
	}
	
	@Override
	public void link( Char ch ) {
		super.link( ch );
		
		if (particles == null) {
			particles = createEmitter();
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if (particles != null){
			particles.visible = visible;
		}
	}
	
	@Override
	public void die() {
		super.die();
		if (particles != null){
			particles.on = false;
		}
	}
	
	@Override
	public void kill() {
		super.kill();
		if (particles != null){
			particles.killAndErase();
		}
	}
	
	public void zap( int cell ) {
		
		turnTo( ch.pos , cell );
		play( zap );
		
		MagicMissile.boltFromChar( parent,
				boltType,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((HereticSummon)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}
	
	@Override
	public void onComplete( Animation anim ) {
		if (anim == zap) {
			idle();
		}
		super.onComplete( anim );
	}
	
	public static class Blood extends HereticSummonSprite {
		
		{
			boltType = MagicMissile.FORCE;
		}
		
		@Override
		protected int texOffset() {
			return 0;
		}
		
		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( BloodParticle.FACTORY, 0.06f );
			return emitter;
		}
	}

	public static class Poison extends HereticSummonSprite {

		{
			boltType = MagicMissile.ELMO;
		}

		@Override
		protected int texOffset() {
			return 10;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( ElmoParticle.FACTORY, 0.06f );
			return emitter;
		}

		@Override
		public int blood() { return 0x4CFF00; }
	}

	public static class Frost extends HereticSummonSprite {

		{
			boltType = MagicMissile.FROST;
		}

		@Override
		protected int texOffset() {
			return 20;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( MagicMissile.MagicParticle.FACTORY, 0.06f );
			return emitter;
		}

		@Override
		public int blood() { return 0x00FFFF; }
	}

	public static class Arcane extends HereticSummonSprite {

		{
			boltType = MagicMissile.WARD;
		}

		@Override
		protected int texOffset() {
			return 30;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( MagicMissile.WardParticle.FACTORY, 0.06f );
			return emitter;
		}

		@Override
		public int blood() { return 0xB200FF; }
	}

	public static class Fire extends HereticSummonSprite {

		{
			boltType = MagicMissile.FIRE;
		}

		@Override
		protected int texOffset() {
			return 40;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( FlameParticle.FACTORY, 0.06f );
			return emitter;
		}

		@Override
		public int blood() {
			return 0xFFFFBB33;
		}
	}

	public static class Dark extends HereticSummonSprite {

		{
			boltType = MagicMissile.SHADOW;
		}

		@Override
		protected int texOffset() {
			return 50;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( ShadowParticle.UP, 0.06f );
			return emitter;
		}

		@Override
		public int blood() { return 0x141414; }
	}

	public static class Earth extends HereticSummonSprite {

		{
			boltType = MagicMissile.EARTH;
		}

		@Override
		protected int texOffset() {
			return 60;
		}

		@Override
		protected Emitter createEmitter() {
			Emitter emitter = emitter();
			emitter.pour( EarthParticle.FALLING, 0.1f );
			return emitter;
		}

		@Override
		public int blood() { return 0x816D5B; }
	}

}
