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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoTalent;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.Callback;

public class TalentButton extends Button {

	public static final int WIDTH = 20;
	public static final int HEIGHT = 26;

	private SmartTexture icons;
	private TextureFilm film;

	Talent talent;
	int pointsInTalent;
	boolean upgradeEnabled;

	Image icon;
	Image bg;

	ColorBlock fill;

	public TalentButton(Talent talent, int points, boolean upgradeEnabled){
		super();
		this.talent = talent;
		this.pointsInTalent = points;
		this.upgradeEnabled = upgradeEnabled;

		icon.frame( film.get( talent.icon() ) );
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		icons = TextureCache.get( Assets.Interfaces.TALENT_ICONS );
		film = new TextureFilm( icons, 16, 16 );

		fill = new ColorBlock(0, 4, 0xFFFFFF44);
		add(fill);

		bg = new Image(Assets.Interfaces.TALENT_BUTTON, 20, 0, WIDTH, HEIGHT);
		add(bg);

		icon = new Image( icons );
		add(icon);
	}

	@Override
	protected void layout() {
		width = WIDTH;
		height = HEIGHT;

		super.layout();

		fill.x = x;
		fill.y = y + WIDTH - 1;
		fill.size( pointsInTalent/2f * WIDTH, 5);

		bg.x = x;
		bg.y = y;

		icon.x = x + 2;
		icon.y = y + 2;
		PixelScene.align(icon);
	}

	@Override
	protected void onClick() {
		super.onClick();

		if (upgradeEnabled
				&& Dungeon.hero != null
				&& Dungeon.hero.talentPointsAvailable() > 0
				&& Dungeon.hero.pointsInTalent(talent) < talent.maxPoints()){
			ShatteredPixelDungeon.scene().addToFront(new WndInfoTalent(talent, pointsInTalent, new Callback() {
				@Override
				public void call() {
					upgradeTalent();
				}
			}));
		} else {
			ShatteredPixelDungeon.scene().addToFront(new WndInfoTalent(talent, pointsInTalent, null));
		}
	}

	@Override
	protected void onPointerDown() {
		icon.brightness( 1.5f );
		bg.brightness( 1.5f );
		Sample.INSTANCE.play( Assets.Sounds.CLICK );
	}

	@Override
	protected void onPointerUp() {
		icon.resetColor();
		bg.resetColor();
	}

	public void enable( boolean value ) {
		active = value;
		icon.alpha( value ? 1.0f : 0.3f );
		bg.alpha( value ? 1.0f : 0.3f );
	}

	public void upgradeTalent(){
		if (Dungeon.hero.talentPointsAvailable() > 0 && parent != null) {
			Dungeon.hero.upgradeTalent(talent);
			float oldWidth = fill.width();
			pointsInTalent++;
			layout();
			Sample.INSTANCE.play(Assets.Sounds.LEVELUP, 0.7f, 1.2f);
			Emitter emitter = (Emitter) parent.recycle(Emitter.class);
			emitter.revive();
			emitter.pos(fill.x + (fill.width() + oldWidth) / 2f, fill.y + fill.height() / 2f);
			emitter.burst(Speck.factory(Speck.STAR), 12);
		}
	}
}