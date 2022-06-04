package fr.catcore.fabricatedforge.mixininterface;

public interface ITessellator {

    void setDefaultTexture(boolean defaultTexture);

    boolean defaultTexture();

    int getTextureID();

    void setTextureID(int textureID);
}
