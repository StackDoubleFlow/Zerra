package com.zerra.common.world.storage;

import com.zerra.client.util.ResourceLocation;
import com.zerra.common.world.World;
import com.zerra.common.world.entity.Entity;
import com.zerra.common.world.storage.plate.Plate;
import com.zerra.common.world.tile.Tile;
import com.zerra.common.world.tile.Tiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2i;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

public class IOManager {

	protected static File saves;
	protected static File instanceDir;

	public static File getSavesDirectory() {
		return saves;
	}

	public static File getInstanceDirectory() {
		return instanceDir;
	}

	public static void init(@Nonnull File folder) {
		File saves = new File(folder, "saves");
		if (!saves.exists()) {
			saves.mkdirs();
			setupDefaultFileSystem(saves);
		}
		IOManager.saves = saves;
		instanceDir = folder;
	}

	private static void setupDefaultFileSystem(File parent) {
		// TODO setup default folders inside the parent folder
	}

	public static class WorldStorageManager {

		private static final int VERSION = 1;

		private final World world;
		private final File worldDir;

		private List<Pair<Integer, ResourceLocation>> tileIndexes;
		private Map<ResourceLocation, Integer> tileMapper;

		public WorldStorageManager(World world) {
			this.world = world;
			this.worldDir = new File(IOManager.saves, world.getName());
			this.tileIndexes = new ArrayList<Pair<Integer, ResourceLocation>>();
			this.tileMapper = new HashMap<ResourceLocation, Integer>();

			this.worldDir.mkdirs();

			//Check save version
			int version = readVersion();
			if(version < VERSION) {
				String[] contents = this.worldDir.list();
				if(contents == null || contents.length <= 0) {
					writeVersion();
				} else {
					//TODO: Convert the world to the new version?
					//But for now we'll ignore until we have converters or another solution in place
					this.world.logger().warn("This world save is an old version! Save version: {}, Game version: {}", version, VERSION);
				}
			}

			try {
				this.readTileIndexes();
			} catch (IOException e) {
				e.printStackTrace();
				this.tileIndexes.clear();
			}
			this.populateTileIndexes();
		}

		private void writeVersion() {
			File file = this.getVersionFile();
			try {
				//noinspection ResultOfMethodCallIgnored
				file.createNewFile();
			} catch (IOException e) {
				this.world.logger().error("Couldn't create new version file", e);
				return;
			}
			try(DataOutputStream is = new DataOutputStream(new FileOutputStream(file))) {
				is.writeInt(VERSION);
			} catch (IOException e) {
				this.world.logger().warn("Error writing to version file!", e);
			}
		}

		private int readVersion() {
			File file = this.getVersionFile();
			try(DataInputStream is = new DataInputStream(new FileInputStream(file))) {
				return is.readInt();
			} catch (IOException ignored) {}
			return -1;
		}

		public void writePlateSafe(int layer, Plate plate) {
			try {
				this.writePlate(layer, plate);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Nullable
        public Plate readPlateSafe(int layer, Vector3ic pos) {
			try {
				return this.readPlate(layer, pos);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

        public void writeEntitiesSafe(int layer, Vector3ic platePos, Set<Entity> entities) {
			try {
				this.writeEntities(layer, platePos, entities);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        public Set<Entity> readEntitiesSafe(int layer, Vector3ic platePos) {
			try {
				return this.readEntities(layer, platePos);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private List<Pair<Integer, ResourceLocation>> loadTiles(File tiles) throws IOException {
			if (tiles.exists()) {
				List<Pair<Integer, ResourceLocation>> pairs = new ArrayList<Pair<Integer, ResourceLocation>>();
				try(DataInputStream is = new DataInputStream(new FileInputStream(tiles))) {
					while (is.available() > 0) {
						pairs.add(new ImmutablePair<Integer, ResourceLocation>((int) is.readShort(), new ResourceLocation(is.readUTF())));
					}
					return pairs;
				}
			} else {
				return new ArrayList<Pair<Integer, ResourceLocation>>();
			}
		}

		private void readTileIndexes() throws IOException {
			// Read tile index from file
			File tileLookupFile = new File(IOManager.saves, this.world.getName() + "/tiles.dat");
			this.tileIndexes.addAll(this.loadTiles(tileLookupFile));
			FileUtils.touch(tileLookupFile);
		}

		private void writeTileIndexes() throws IOException {
			// Write tile lookup
			File tileLookupFile = new File(IOManager.saves, this.world.getName() + "/tiles.dat");
			FileUtils.touch(tileLookupFile);

			try(DataOutputStream os = new DataOutputStream(new FileOutputStream(tileLookupFile))) {
				// Write all tiles to the lookup file
				for (Pair<Integer, ResourceLocation> pair : this.tileIndexes) {
					os.writeShort(pair.getLeft());
					os.writeUTF(pair.getRight().toString());
				}
			}
		}

		private void populateTileIndexes() {
			// Add missing tiles to map
			for (Tile tile : Tiles.getTiles()) {
				int id = -1;
				for (int i = 0; i < this.tileIndexes.size(); i++) {
					if (this.tileIndexes.get(i).getRight().equals(tile.getRegistryID())) {
						id = this.tileIndexes.get(i).getLeft();
						this.tileMapper.put(tile.getRegistryID(), id);
						break;
					}
				}

				if (id == -1) {
					this.tileIndexes.add(new ImmutablePair<Integer, ResourceLocation>(this.tileIndexes.size(), tile.getRegistryID()));
					this.tileMapper.put(tile.getRegistryID(), this.tileIndexes.size() - 1);
				}
			}

			try {
				this.writeTileIndexes();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void writePlate(int layer, Plate plate) throws IOException {
			// Create plate file
			File plateFile = this.getPlateFile(layer, plate.getPlatePos());
			FileUtils.touch(plateFile);

			try(DataOutputStream os = new DataOutputStream(new FileOutputStream(plateFile))) {
				// Write plate tiles
				for (int x = 0; x < Plate.SIZE; x++) {
					for (int z = 0; z < Plate.SIZE; z++) {
						Vector2i position = new Vector2i(x, z);
						os.writeShort(this.tileIndexes.get(this.tileMapper.get(plate.getTileAt(position).getRegistryID())).getLeft());
					}
				}
			}
		}

		@Nullable
        public Plate readPlate(int layer, Vector3ic platePos) throws IOException {
			// Create plate file
			File plateFile = this.getPlateFile(layer, platePos);
			if (!plateFile.exists()) {
				return null;
			}
			try(DataInputStream is = new DataInputStream(new FileInputStream(plateFile))) {
				// Read plate tiles
				Plate plate = new Plate(this.world.getLayer(layer));
				for (int x = 0; x < Plate.SIZE; x++) {
					for (int z = 0; z < Plate.SIZE; z++) {
						Vector2i tilePos = new Vector2i(x, z);
						plate.setTileAt(tilePos, Tiles.byId(this.tileIndexes.get(is.readShort()).getRight()));
					}
				}
				plate.setPlatePos(platePos);
				return plate;
			}
		}

        public void writeEntities(int layer, Vector3ic platePos, Set<Entity> entities) throws IOException {
            File file = getEntityFile(layer, platePos);
		    if (entities.isEmpty()) {
		        if (file.exists()) {
		            file.delete();
                }
		        return;
            }
			FileUtils.touch(file);
			//TODO: Write entities to file
		}

        public Set<Entity> readEntities(int layer, Vector3ic platePos) throws IOException {
			File file = getEntityFile(layer, platePos);
			if (!file.exists()) {
			    return Collections.emptySet();
            }
			//TODO: Read entities from file

			return new HashSet<>();
		}

        public void backupPlate(int layer, Vector3ic pos) {
			try {
				// Look for the plate file to back up
				File plateFile = this.getPlateFile(layer, pos);
				if (plateFile.exists()) {
                    File backupPlateFile = new File(IOManager.saves, this.world.getName() + "/plates-" + layer + "-bak/" + pos.x() + "_" + pos.y() + "_" + pos.z() + ".zpl");
					FileUtils.touch(backupPlateFile);
					org.apache.commons.io.IOUtils.copyLarge(new FileInputStream(plateFile), new FileOutputStream(backupPlateFile));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

        public boolean isPlateGenerated(int layer, Vector3ic pos) {
			return this.getPlateFile(layer, pos).exists();
		}

		private File getLayerDir(int layer) {
			return new File(this.worldDir, "plates-" + layer);
		}

        private File getPlateFile(int layer, Vector3ic pos) {
			return new File(getLayerDir(layer), "plate_" + pos.x() + "_" + pos.y() + "_" + pos.z() + ".zpl");
		}

        private File getEntityFile(int layer, Vector3ic platePos) {
			return new File(getLayerDir(layer), "entities_" + platePos.x() + "_" + platePos.y() + "_" + platePos.z() + ".zen");
		}

		private File getVersionFile() {
			return new File(this.worldDir, "version.zsv");
		}

		public World getWorld() {
			return world;
		}
	}
}