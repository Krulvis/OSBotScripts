package api.wrappers;

import api.util.cache.CacheLoader;
import api.util.cache.InputStream;
import api.util.cache.Stream;

/**
 * Created by Krulvis on 08-Jun-17.
 */
public class ItemDef {

    private int itemID;
    private int certTemplateId = -1;
    private int certReferenceId = -1;
    private int team = 0;
    private String name = "null";
    private String[] groundActions = null;
    private String[] inventoryActions = null;
    private int isStackable = 0;
    private int value = 1;
    private boolean isMembersOnly = false;
    private int inventoryModelID;
    private short[] modelRecolorOriginal;
    private short[] modelRecolorTarget;
    private int[] stackVariantSize;
    private short[] modelTextureTarget;
    private int rotationLength = 2000;
    private int rotationX = 0;
    private int rotationY = 0;
    private int rotationZ = 0;
    private int translateX = 0;
    private int translateY = 0;
    private int modelScaleX = 128;
    private int modelScaleY = 128;
    private int modelScaleZ = 128;
    private int equippedModelMale1;
    private int equippedModelMale2;
    private int equippedModelMale3;
    private int equippedModelMaleTranslationY;
    private int equippedModelFemale1;
    private int equippedModelFemale2;
    private int equippedModelFemale3;
    private int equippedModelFemaleTranslationY;
    private int equippedModelMaleDialogue1;
    private int equippedModelMaleDialogue2;
    private int equippedModelFemaleDialogue1;
    private int equippedModelFemaleDialogue2;
    private int lightIntensity;
    private int lightMag;
    private int[] stackVariantID;
    private short[] modelTextureOriginal;
    private int placeholderReferenceId;
    private int placeholderTemplateId;

    private static CacheLoader cacheLoader;
    private static ItemDef[] cache;

    public static void setupCacheLoader(CacheLoader cacheLoader_) {
        cacheLoader = cacheLoader_;
        cache = new ItemDef[cacheLoader.getValidFilesCount(10)];
    }

    public static ItemDef get(final int id) {
        if (id < 0 || id >= cache.length) {
            return null;
        }
        ItemDef itemDef = cache[id];
        if (itemDef != null) {
            return itemDef;
        }
        final byte[] data = cacheLoader.getFile(10, id);
        if (data == null) {
            return null;
        }
        itemDef = new ItemDef(id, new InputStream(data));
        if (itemDef.certTemplateId != -1) {
            itemDef.formNotedItem(get(itemDef.certTemplateId), get(itemDef.certReferenceId));
        }
        cache[id] = itemDef;
        return itemDef;
    }

    private void formNotedItem(final ItemDef notedPaperModel, final ItemDef unnotedItem) {
        if (notedPaperModel == null || unnotedItem == null) {
            return;
        }
        this.inventoryModelID = notedPaperModel.inventoryModelID;
        this.rotationLength = notedPaperModel.rotationLength;
        this.rotationX = notedPaperModel.rotationX;
        this.rotationY = notedPaperModel.rotationY;
        this.rotationZ = notedPaperModel.rotationZ;
        this.translateX = notedPaperModel.translateX;
        this.translateY = notedPaperModel.translateY;
        this.modelRecolorOriginal = notedPaperModel.modelRecolorOriginal;
        this.modelRecolorTarget = notedPaperModel.modelRecolorTarget;
        this.modelTextureOriginal = notedPaperModel.modelTextureOriginal;
        this.modelTextureTarget = notedPaperModel.modelTextureTarget;
        this.name = unnotedItem.name;
        this.isMembersOnly = unnotedItem.isMembersOnly;
        this.value = unnotedItem.value;
        this.isStackable = 1;
        unnotedItem.certTemplateId = this.getID();
    }

    public ItemDef(int id, Stream str) {
        loadFromStream(str);
        this.itemID = id;
    }

    private void loadFromStream(Stream var1) {
        while (true) {
            int var3 = var1.readUnsignedByte();
            if (var3 == 0) {
                return;
            }
            this.loadFromStream(var1, var3);
        }
    }

    private void loadFromStream(Stream stream, int opcode) {
        if (opcode == 1) {
            this.inventoryModelID = stream.readUnsignedShort();
        } else if (opcode == 2) {
            this.name = stream.readString(1297377970);
        } else if (opcode == 4) {
            this.rotationLength = stream.readUnsignedShort();
        } else if (opcode == 5) {
            this.rotationX = stream.readUnsignedShort();
        } else if (opcode == 6) {
            this.rotationY = stream.readUnsignedShort();
        } else if (opcode == 7) {
            this.translateX = stream.readUnsignedShort();
            if (this.translateX > 32767) {
                this.translateX -= 0x10000;
            }
        } else if (opcode == 8) {
            this.translateY = stream.readUnsignedShort();
            if (this.translateY > 32767) {
                this.translateY -= 0x10000;
            }
        } else if (11 == opcode) {
            this.isStackable = 1;
        } else if (12 == opcode) {
            this.value = stream.readUnsignedInt(-1975105290);
        } else if (opcode == 16) {
            this.isMembersOnly = true;
        } else if (23 == opcode) {
            this.equippedModelMale1 = stream.readUnsignedShort();
            this.equippedModelMaleTranslationY = stream.readUnsignedByte();
        } else if (24 == opcode) {
            this.equippedModelMale2 = stream.readUnsignedShort();
        } else if (opcode == 25) {
            this.equippedModelFemale1 = stream.readUnsignedShort();
            this.equippedModelFemaleTranslationY = stream.readUnsignedByte();
        } else if (opcode == 26) {
            this.equippedModelFemale2 = stream.readUnsignedShort();
        } else if (opcode >= 30 && opcode < 35) {
            if (this.groundActions == null) {
                this.groundActions = new String[5];
            }
            this.groundActions[opcode - 30] = stream.readString(1886948725);
            if (this.groundActions[opcode - 30].equalsIgnoreCase("Hidden")) {
                this.groundActions[opcode - 30] = null;
            }
        } else if (opcode >= 35 && opcode < 40) {
            if (this.inventoryActions == null) {
                this.inventoryActions = new String[5];
            }
            this.inventoryActions[opcode - 35] = stream.readString(868536486);
        } else if (opcode == 40) {
            int amountOfColors = stream.readUnsignedByte();
            this.modelRecolorOriginal = new short[amountOfColors];
            this.modelRecolorTarget = new short[amountOfColors];
            for (int var5 = 0; var5 < amountOfColors; ++var5) {
                this.modelRecolorOriginal[var5] = (short) stream.readUnsignedShort();
                this.modelRecolorTarget[var5] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 41) {
            int var4 = stream.readUnsignedByte();
            this.modelTextureOriginal = new short[var4];
            this.modelTextureTarget = new short[var4];
            for (int var5 = 0; var5 < var4; ++var5) {
                this.modelTextureOriginal[var5] = (short) stream.readUnsignedShort();
                this.modelTextureTarget[var5] = (short) stream.readUnsignedShort();
            }
        } else if (opcode == 78) {
            this.equippedModelMale3 = stream.readUnsignedShort();
        } else if (79 == opcode) {
            this.equippedModelFemale3 = stream.readUnsignedShort();
        } else if (opcode == 90) {
            this.equippedModelMaleDialogue1 = stream.readUnsignedShort();
        } else if (91 == opcode) {
            this.equippedModelFemaleDialogue1 = stream.readUnsignedShort();
        } else if (opcode == 92) {
            this.equippedModelMaleDialogue2 = stream.readUnsignedShort();
        } else if (93 == opcode) {
            this.equippedModelFemaleDialogue2 = stream.readUnsignedShort();
        } else if (95 == opcode) {
            this.rotationZ = stream.readUnsignedShort();
        } else if (opcode == 97) {
            this.certReferenceId = stream.readUnsignedShort();
        } else if (98 == opcode) {
            this.certTemplateId = stream.readUnsignedShort();
        } else if (opcode >= 100 && opcode < 110) {
            if (this.stackVariantID == null) {
                this.stackVariantID = new int[10];
                this.stackVariantSize = new int[10];
            }
            this.stackVariantID[opcode - 100] = stream.readUnsignedShort();
            this.stackVariantSize[opcode - 100] = stream.readUnsignedShort();
        } else if (opcode == 110) {
            this.modelScaleX = stream.readUnsignedShort();
        } else if (111 == opcode) {
            this.modelScaleY = stream.readUnsignedShort();
        } else if (112 == opcode) {
            this.modelScaleZ = stream.readUnsignedShort();
        } else if (113 == opcode) {
            this.lightIntensity = stream.ag();
        } else if (opcode == 114) {
            this.lightMag = stream.ag() * 5;
        } else if (opcode == 115) {
            this.team = stream.readUnsignedByte();
        } else if (opcode == 148) {
            placeholderReferenceId = stream.readUnsignedShort();
        } else if (opcode == 149) {
            placeholderTemplateId = stream.readUnsignedShort();
        }
    }

    public int getEquippedModelFemaleTranslationY() {
        return equippedModelFemaleTranslationY;
    }

    public String[] getGroundActions() {
        return groundActions;
    }

    public int getEquippedModelFemaleDialogue2() {
        return equippedModelFemaleDialogue2;
    }

    public int getEquippedModelMaleDialogue1() {
        return equippedModelMaleDialogue1;
    }

    public int getInventoryModelID() {
        return inventoryModelID;
    }

    public String[] getActions() {
        return inventoryActions;
    }

    public short[] getModelRecolorOriginal() {
        return modelRecolorOriginal;
    }

    public short[] getModelRecolorTarget() {
        return modelRecolorTarget;
    }

    public int[] getStackVariantSize() {
        return stackVariantSize;
    }

    public int getRotationLength() {
        return rotationLength;
    }

    public int getRotationY() {
        return rotationY;
    }

    public int getRotationZ() {
        return rotationZ;
    }

    public int getTranslateX() {
        return translateX;
    }

    public int getTranslateY() {
        return translateY;
    }

    public boolean isStackable() {
        return isStackable == 1;
    }

    @Deprecated
    public int getValue() {
        return value;
    }

    public int getStoreValue() {
        return value;
    }

    public int getLowAlchPrice() {
        return (int) (value * (0.6 * 0.5));
    }

    public int getHighAlchPrice() {
        return (int) (value * 0.6);
    }

    public boolean isMembersOnly() {
        return isMembersOnly;
    }

    public int getEquippedModelMale3() {
        return equippedModelMale3;
    }

    public int getEquippedModelMale1() {
        return equippedModelMale1;
    }

    public String getName() {
        return name;
    }

    public int getEquippedModelMaleTranslationY() {
        return equippedModelMaleTranslationY;
    }

    public int getEquippedModelFemale1() {
        return equippedModelFemale1;
    }

    public int getLightIntensity() {
        return lightIntensity;
    }

    public int getID() {
        return itemID;
    }

    public int getRotationX() {
        return rotationX;
    }

    public int getEquippedModelFemale3() {
        return equippedModelFemale3;
    }

    public int getLightMag() {
        return lightMag;
    }

    public int getEquippedModelMaleDialogue2() {
        return equippedModelMaleDialogue2;
    }

    /**
     * Use {@link #getUncertedItemId()} instead
     */
    @Deprecated
    public int getUnnotedItemID() {
        return certReferenceId;
    }

    public int[] getStackVariantID() {
        return stackVariantID;
    }

    public int getEquippedModelMale2() {
        return equippedModelMale2;
    }

    /**
     * Use {@link #getCertedItemId()} instead
     */
    @Deprecated
    public int getNotedItemID() {
        return certTemplateId;
    }

    public int getModelScaleX() {
        return modelScaleX;
    }

    public int getModelScaleY() {
        return modelScaleY;
    }

    public int getModelScaleZ() {
        return modelScaleZ;
    }

    public int getEquippedModelFemaleDialogue1() {
        return equippedModelFemaleDialogue1;
    }

    public int getTeam() {
        return team;
    }

    public int getEquippedModelFemale2() {
        return equippedModelFemale2;
    }

    /**
     * Returns the cert reference id. If you want to get the certed/uncerted ids check {@link #getUncertedItemId()} and {@link #getCertedItemId()}
     */
    public int getCertReferenceId() {
        return certReferenceId;
    }

    /**
     * Returns the cert template id
     */
    public int getCertTemplateId() {
        return certTemplateId;
    }

    /**
     * Returns the bank cert id for this item. Returns -1 if this item is already a bank cert.
     *
     * @return
     */
    public int getCertedItemId() {

        if (!isCert()) {
            return certReferenceId;
        }
        return -1;
    }

    /**
     * Returns the item id that this item represents. Returns -1 if this item is not a bank cert.
     *
     * @return
     */
    public int getUncertedItemId() {

        if (isCert()) {
            return certReferenceId;
        }
        return -1;
    }

    /**
     * Returns if this item is a bank cert or not.
     *
     * @return
     */
    public boolean isCert() {
        return certTemplateId != -1;
    }

    public int getPlaceholderReferenceId() {
        return placeholderReferenceId;
    }

    public int getPlaceholderTemplateId() {
        return placeholderTemplateId;
    }

    public int getPlaceholderItemId() {

        if (!isPlaceholder()) {
            return placeholderReferenceId;
        }
        return -1;
    }

    public int getPlaceholdedItemId() {

        if (isPlaceholder()) {
            return placeholderReferenceId;
        }
        return -1;
    }

    public boolean isPlaceholder() {
        return placeholderTemplateId != 0;
    }
}
