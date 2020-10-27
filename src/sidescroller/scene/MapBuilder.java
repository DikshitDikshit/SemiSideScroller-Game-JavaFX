package sidescroller.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javafx.scene.canvas.Canvas;
import sidescroller.entity.GenericEntity;
import sidescroller.entity.property.Entity;
import sidescroller.entity.property.HitBox;
import sidescroller.entity.sprite.BackgroundSprite;
import sidescroller.entity.sprite.LandSprite;
import sidescroller.entity.sprite.PlatformSprite;
import sidescroller.entity.sprite.SpriteFactory;
import sidescroller.entity.sprite.TreeSprite;
import sidescroller.entity.sprite.tile.Tile;
import utility.Tuple;

public class MapBuilder implements MapBuilderInterface {

	private Tuple rowColCount;
	private Tuple dimension;
	private double scale;
	private Canvas canvas;
	private Entity background;
	private List<Entity> landMass;
	private List<Entity> other;

	protected MapBuilder() {
		landMass = new ArrayList<Entity>();
		other = new ArrayList<Entity>();
	}

	public static MapBuilder createBuilder() {
		return new MapBuilder();
	}

	@Override
	public MapBuilder setCanvas(Canvas canvas) {
		this.canvas = canvas;
		return this;
	}

	@Override
	public MapBuilder setGrid(Tuple rowColCount, Tuple dimension) {
		this.rowColCount = rowColCount;
		this.dimension = dimension;

		return this;
	}

	@Override
	public MapBuilder setGridScale(double scale) {
		this.scale = scale;
		return this;
	}

	@Override
	public MapBuilder buildBackground(BiFunction<Integer, Integer, Tile> callback) {
		BackgroundSprite bsp = SpriteFactory.get("Background");
		bsp.init(scale, dimension, Tuple.pair(0.0, 0.0));
		bsp.createSnapshot(canvas, rowColCount, callback);
		HitBox hb = HitBox.build(0, 0, scale * dimension.x() * rowColCount.y(),
				scale * dimension.y() * rowColCount.x());
		background = new GenericEntity(bsp, hb);
		return this;
	}

	@Override
	public MapBuilder buildLandMass(int rowPos, int colPos, int rowConut, int colCount) {
		LandSprite land = SpriteFactory.get("Land");
		
		land.init(scale, dimension, Tuple.pair(colPos, rowPos));
		land.createSnapshot(canvas, rowConut, colCount);
		HitBox hb = HitBox.build(colPos * dimension.x() * scale, rowPos * dimension.y() * scale,
				scale * dimension.x() * colCount, scale * dimension.y() * rowConut);
		landMass.add(new GenericEntity(land, hb));
		return this;
	}

	@Override
	public MapBuilder buildTree(int rowPos, int colPos, Tile tile) {
		TreeSprite tree = SpriteFactory.get("Tree");
		tree.init(scale, dimension, Tuple.pair(colPos, rowPos));
		tree.createSnapshot(canvas, tile);
		other.add(new GenericEntity(tree, null));
		return this;
	}

	@Override
	public MapBuilder buildPlatform(int rowPos, int colPos, int length, Tile tile) {
		PlatformSprite platform = SpriteFactory.get("Platform");
		platform.init(scale, dimension, Tuple.pair(colPos, rowPos));
		platform.createSnapshot(canvas, tile, length);
		HitBox hb = HitBox.build((colPos + .5) * dimension.x() * scale, rowPos * dimension.y() * scale,
				scale * dimension.x() * (length - 1), scale * dimension.y() / 2);
		other.add(new GenericEntity(platform, hb));
		return this;
	}

	@Override
	public Entity getBackground() {
		
		return background;
	}

	@Override
	public List<Entity> getEntities(List<Entity> list) {
		list.addAll(landMass);
		list.addAll(other);
		return list;
	}

}
