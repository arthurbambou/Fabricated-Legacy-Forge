package fr.catcore.fabricatedforge.mixin.forgefml.client;

import net.minecraft.client.ResourceDownloadThread;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@Mixin(ResourceDownloadThread.class)
public abstract class ResourceDownloadThreadMixin {

    @Shadow protected abstract void method_802(URL baseURL, String resourcePath, long l, int i);

    @Shadow private boolean field_886;

    @Shadow protected abstract void method_800(File file, String string);

    @Shadow public File gameFolder;

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    public void run() {
        try {
            URL var1 = new URL("http://s3.amazonaws.com/MinecraftResources/");
            DocumentBuilderFactory var2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder var3 = var2.newDocumentBuilder();
            URLConnection con = var1.openConnection();
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);
            Document var4 = var3.parse(con.getInputStream());
            NodeList var5 = var4.getElementsByTagName("Contents");

            for(int var6 = 0; var6 < 2; ++var6) {
                for(int var7 = 0; var7 < var5.getLength(); ++var7) {
                    Node var8 = var5.item(var7);
                    if (var8.getNodeType() == 1) {
                        Element var9 = (Element)var8;
                        String var10 = var9.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                        long var11 = Long.parseLong(var9.getElementsByTagName("Size").item(0).getChildNodes().item(0).getNodeValue());
                        if (var11 > 0L) {
                            this.method_802(var1, var10, var11, var6);
                            if (this.field_886) {
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception var14) {
            this.method_800(this.gameFolder, "");
            var14.printStackTrace();
        }

    }

    /**
     * @author Minecraft Forge
     * @reason none
     */
    @Overwrite
    private void method_801(URL par1URL, File par2File, long par3) throws IOException {
        byte[] var5 = new byte[4096];
        URLConnection con = par1URL.openConnection();
        con.setConnectTimeout(60000);
        con.setReadTimeout(60000);
        DataInputStream var6 = new DataInputStream(con.getInputStream());
        DataOutputStream var7 = new DataOutputStream(Files.newOutputStream(par2File.toPath()));
        boolean var8 = false;

        int var9;
        while((var9 = var6.read(var5)) >= 0) {
            var7.write(var5, 0, var9);
            if (this.field_886) {
                return;
            }
        }

        var6.close();
        var7.close();
    }
}
