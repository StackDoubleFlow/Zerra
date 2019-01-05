package com.zerra.common.world.storage;

import com.zerra.client.util.ResourceLocation;
import com.zerra.common.CrashCodes;
import com.zerra.common.world.World;
import com.zerra.common.world.storage.plate.Plate;
import com.zerra.common.world.tile.Tile;
import com.zerra.common.world.tile.Tiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.joml.Vector2i;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOManager {

    private static final Logger LOGGER = Logger.getLogger("IO");
    private static File saves;
    private static File instanceDir;

    private IOManager() {
        //hidden constructor
    }

    public static File getSavesDirectory() {
        return saves;
    }

    public static File getInstanceDirectory() {
        return instanceDir;
    }

    public static void init(@Nonnull File folder) {
        File saves = new File(folder, "saves");
        if (!saves.exists()) {
            if (saves.mkdirs()) {
                setupDefaultFileSystem(saves);
            } else {
                LOGGER.error("failed to create the required folders!!");
                LOGGER.info("exiting program...");
                System.exit(CrashCodes.IO_EXCEPTION);
            }
        }
        IOManager.saves = saves;
        instanceDir = folder;
    }

    private static void setupDefaultFileSystem(File parent) {
        // TODO setup default folders inside the parent folder
    }

    public static class WorldStorageManager {

        public static final short VERSION = 0;

        private final World world;

        private List<Pair<Integer, ResourceLocation>> tileIndexes;
        private Map<ResourceLocation, Integer> tileMapper;

        public WorldStorageManager(World world) {
            this.world = world;
            this.tileIndexes = new ArrayList<>();
            this.tileMapper = new HashMap<>();

            try {
                this.readTileIndexes();
            } catch (IOException e) {
                LOGGER.error("Caught exception while trying to read Tiles:", e);
                this.tileIndexes.clear();
            }
            this.populateTileIndexes();
        }

        public void writePlateSafe(int layer, Plate plate) {
            try {
                this.writePlate(layer, plate);
            } catch (IOException e) {
                LOGGER.error("Caught exception while trying to write a plate:", e);
            }
        }

        @Nullable
        public Plate readPlateSafe(int layer, Vector3i pos) {
            try {
                return this.readPlate(layer, pos);
            } catch (IOException e) {
                LOGGER.error("Caught exception while trying to read a plate:", e);
                return null;
            }
        }

        private List<Pair<Integer, ResourceLocation>> loadTiles(File tiles) throws IOException {
            if (tiles.exists()) {
                List<Pair<Integer, ResourceLocation>> pairs = new ArrayList<>();
                DataInputStream is = new DataInputStream(new FileInputStream(tiles));
                while (is.available() > 0) {
                    pairs.add(new ImmutablePair<>((int) is.readShort(), new ResourceLocation(is.readUTF())));
                }
                is.close();
                return pairs;
            } else {
                return new ArrayList<>();
            }
        }

        private void readTileIndexes() throws IOException {
            /* Read tile index from file */
            File tileLookupFile = new File(IOManager.saves, this.world.getName() + "/tiles.dat");
            this.tileIndexes.addAll(this.loadTiles(tileLookupFile));
            FileUtils.touch(tileLookupFile);
        }

        private void writeTileIndexes() throws IOException {
            /* Write tile lookup */
            File tileLookupFile = new File(IOManager.saves, this.world.getName() + "/tiles.dat");
            FileUtils.touch(tileLookupFile);

            {
                DataOutputStream os = new DataOutputStream(new FileOutputStream(tileLookupFile));
                /* Write all tiles to the lookup file */
                for (Pair<Integer, ResourceLocation> pair : this.tileIndexes) {
                    os.writeShort(pair.getLeft());
                    os.writeUTF(pair.getRight().toString());
                }
                os.close();
            }
        }

        private void populateTileIndexes() {
            /* Add missing tiles to map */
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
                LOGGER.error("Caught exception while trying to write tile indexes:", e);
            }
        }

        void writePlate(int layer, Plate plate) throws IOException {
            Vector3i pos = plate.getPlatePos();

            /* Create plate file */
            File plateFile = new File(IOManager.saves, this.world.getName() + "/plates-" + layer + '/' + pos.x + "-" + pos.y + "-" + pos.z + ".zpl");
            FileUtils.touch(plateFile);

            {
                DataOutputStream os = new DataOutputStream(new FileOutputStream(plateFile));
                /* Write version */
                os.writeShort(VERSION);
                /* Write plate tiles */
                for (int x = 0; x < Plate.SIZE; x++) {
                    for (int z = 0; z < Plate.SIZE; z++) {
                        Vector2i position = new Vector2i(x, z);
                        os.writeShort(this.tileIndexes.get(this.tileMapper.get(plate.getTileAt(position).getRegistryID())).getLeft());
                    }
                }
                os.close();
            }
        }

        @Nullable
        Plate readPlate(int layer, Vector3i pos) throws IOException {
            /* Create plate file */
            File plateFile = new File(IOManager.saves, this.world.getName() + "/plates-" + layer + "/" + pos.x + "-" + pos.y + "-" + pos.z + ".zpl");
            if (plateFile.exists()) {
                DataInputStream is = new DataInputStream(new FileInputStream(plateFile));
                Plate plate = new Plate(this.world.getLayer(layer));
                for (int x = 0; x < Plate.SIZE; x++) {
                    for (int z = 0; z < Plate.SIZE; z++) {
                        Vector2i tilePos = new Vector2i(x, z);
                        plate.setTileAt(tilePos, Tiles.byId(this.tileIndexes.get(is.readInt()).getRight()));
                    }
                }
                plate.setPlatePos(pos);
                is.close();
                return plate;
            } else {
                return null;
            }
        }

        public boolean isPlateGenerated(int layer, Vector3i pos) {
            File file = new File(IOManager.saves, this.world.getName() + "/plates-" + layer + "/" + pos.x + "-" + pos.y + "-" + pos.z + ".zpl");
            return file.exists();
        }

        public World getWorld() {
            return world;
        }
    }
}