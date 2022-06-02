package cpw.mods.fml.common.asm.transformers;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class MCPMerger {
    private static Hashtable<String, MCPMerger.ClassInfo> clients = new Hashtable();
    private static Hashtable<String, MCPMerger.ClassInfo> shared = new Hashtable();
    private static Hashtable<String, MCPMerger.ClassInfo> servers = new Hashtable();
    private static HashSet<String> copyToServer = new HashSet();
    private static HashSet<String> copyToClient = new HashSet();
    private static final boolean DEBUG = false;

    public MCPMerger() {
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: MCPMerger <MapFile> <minecraft.jar> <minecraft_server.jar>");
            System.exit(1);
        }

        File map_file = new File(args[0]);
        File client_jar = new File(args[1]);
        File server_jar = new File(args[2]);
        File client_jar_tmp = new File(args[1] + ".MergeBack");
        File server_jar_tmp = new File(args[2] + ".MergeBack");
        if (client_jar_tmp.exists() && !client_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + client_jar_tmp);
        }

        if (server_jar_tmp.exists() && !server_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + server_jar_tmp);
        }

        if (!client_jar.exists()) {
            System.out.println("Could not find minecraft.jar: " + client_jar);
            System.exit(1);
        }

        if (!server_jar.exists()) {
            System.out.println("Could not find minecraft_server.jar: " + server_jar);
            System.exit(1);
        }

        if (!client_jar.renameTo(client_jar_tmp)) {
            System.out.println("Could not rename file: " + client_jar + " -> " + client_jar_tmp);
            System.exit(1);
        }

        if (!server_jar.renameTo(server_jar_tmp)) {
            System.out.println("Could not rename file: " + server_jar + " -> " + server_jar_tmp);
            System.exit(1);
        }

        if (!readMapFile(map_file)) {
            System.out.println("Could not read map file: " + map_file);
            System.exit(1);
        }

        try {
            processJar(client_jar_tmp, server_jar_tmp, client_jar, server_jar);
        } catch (IOException var7) {
            var7.printStackTrace();
            System.exit(1);
        }

        if (!client_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + client_jar_tmp);
        }

        if (!server_jar_tmp.delete()) {
            System.out.println("Could not delete temp file: " + server_jar_tmp);
        }

    }

    private static boolean readMapFile(File mapFile) {
        try {
            FileInputStream fstream = new FileInputStream(mapFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = br.readLine()) != null) {
                boolean toClient = line.charAt(0) == '<';
                line = line.substring(1);
                if (toClient) {
                    copyToClient.add(line);
                } else {
                    copyToServer.add(line);
                }
            }

            in.close();
            return true;
        } catch (Exception var6) {
            System.err.println("Error: " + var6.getMessage());
            return false;
        }
    }

    public static void processJar(File clientInFile, File serverInFile, File clientOutFile, File serverOutFile) throws IOException {
        ZipFile cInJar = null;
        ZipFile sInJar = null;
        ZipOutputStream cOutJar = null;
        ZipOutputStream sOutJar = null;

        try {
            try {
                cInJar = new ZipFile(clientInFile);
                sInJar = new ZipFile(serverInFile);
            } catch (FileNotFoundException var40) {
                throw new FileNotFoundException("Could not open input file: " + var40.getMessage());
            }

            try {
                cOutJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(clientOutFile)));
                sOutJar = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(serverOutFile)));
            } catch (FileNotFoundException var39) {
                throw new FileNotFoundException("Could not open output file: " + var39.getMessage());
            }

            Hashtable<String, ZipEntry> cClasses = getClassEntries(cInJar, cOutJar);
            Hashtable<String, ZipEntry> sClasses = getClassEntries(sInJar, sOutJar);
            HashSet<String> cAdded = new HashSet();
            HashSet<String> sAdded = new HashSet();

            for(Entry<String, ZipEntry> entry : cClasses.entrySet()) {
                String name = (String)entry.getKey();
                ZipEntry cEntry = (ZipEntry)entry.getValue();
                ZipEntry sEntry = (ZipEntry)sClasses.get(name);
                if (sEntry == null) {
                    if (!copyToServer.contains(name)) {
                        copyClass(cInJar, cEntry, cOutJar, null, true);
                        cAdded.add(name);
                    } else {
                        copyClass(cInJar, cEntry, cOutJar, sOutJar, true);
                        cAdded.add(name);
                        sAdded.add(name);
                    }
                } else {
                    sClasses.remove(name);
                    MCPMerger.ClassInfo info = new MCPMerger.ClassInfo(name);
                    shared.put(name, info);
                    byte[] cData = readEntry(cInJar, (ZipEntry)entry.getValue());
                    byte[] sData = readEntry(sInJar, sEntry);
                    byte[] data = processClass(cData, sData, info);
                    ZipEntry newEntry = new ZipEntry(cEntry.getName());
                    cOutJar.putNextEntry(newEntry);
                    cOutJar.write(data);
                    sOutJar.putNextEntry(newEntry);
                    sOutJar.write(data);
                    cAdded.add(name);
                    sAdded.add(name);
                }
            }

            for(Entry<String, ZipEntry> entry : sClasses.entrySet()) {
                if (!copyToClient.contains(entry.getKey())) {
                    copyClass(sInJar, (ZipEntry)entry.getValue(), null, sOutJar, false);
                } else {
                    copyClass(sInJar, (ZipEntry)entry.getValue(), cOutJar, sOutJar, false);
                }
            }

            for(String name : new String[]{SideOnly.class.getName(), Side.class.getName()}) {
                String eName = name.replace(".", "/");
                byte[] data = getClassBytes(name);
                ZipEntry newEntry = new ZipEntry(name.replace(".", "/").concat(".class"));
                if (!cAdded.contains(eName)) {
                    cOutJar.putNextEntry(newEntry);
                    cOutJar.write(data);
                }

                if (!sAdded.contains(eName)) {
                    sOutJar.putNextEntry(newEntry);
                    sOutJar.write(data);
                }
            }
        } finally {
            if (cInJar != null) {
                try {
                    cInJar.close();
                } catch (IOException var38) {
                }
            }

            if (sInJar != null) {
                try {
                    sInJar.close();
                } catch (IOException var37) {
                }
            }

            if (cOutJar != null) {
                try {
                    cOutJar.close();
                } catch (IOException var36) {
                }
            }

            if (sOutJar != null) {
                try {
                    sOutJar.close();
                } catch (IOException var35) {
                }
            }

        }

    }

    private static void copyClass(ZipFile inJar, ZipEntry entry, ZipOutputStream outJar, ZipOutputStream outJar2, boolean isClientOnly) throws IOException {
        ClassReader reader = new ClassReader(readEntry(inJar, entry));
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        if (!classNode.name.equals("bcs")) {
            if (classNode.visibleAnnotations == null) {
                classNode.visibleAnnotations = new ArrayList();
            }

            classNode.visibleAnnotations.add(getSideAnn(isClientOnly));
        }

        ClassWriter writer = new ClassWriter(1);
        classNode.accept(writer);
        byte[] data = writer.toByteArray();
        ZipEntry newEntry = new ZipEntry(entry.getName());
        if (outJar != null) {
            outJar.putNextEntry(newEntry);
            outJar.write(data);
        }

        if (outJar2 != null) {
            outJar2.putNextEntry(newEntry);
            outJar2.write(data);
        }

    }

    private static AnnotationNode getSideAnn(boolean isClientOnly) {
        AnnotationNode ann = new AnnotationNode(Type.getDescriptor(SideOnly.class));
        ann.values = new ArrayList();
        ann.values.add("value");
        ann.values.add(new String[]{Type.getDescriptor(Side.class), isClientOnly ? "CLIENT" : "SERVER"});
        return ann;
    }

    private static Hashtable<String, ZipEntry> getClassEntries(ZipFile inFile, ZipOutputStream outFile) throws IOException {
        Hashtable<String, ZipEntry> ret = new Hashtable();

        for(ZipEntry entry : Collections.list(inFile.entries())) {
            if (entry.isDirectory()) {
                outFile.putNextEntry(entry);
            } else {
                String entryName = entry.getName();
                if (entryName.endsWith(".class") && !entryName.startsWith(".")) {
                    ret.put(entryName.replace(".class", ""), entry);
                } else {
                    ZipEntry newEntry = new ZipEntry(entry.getName());
                    outFile.putNextEntry(newEntry);
                    outFile.write(readEntry(inFile, entry));
                }
            }
        }

        return ret;
    }

    private static byte[] readEntry(ZipFile inFile, ZipEntry entry) throws IOException {
        return readFully(inFile.getInputStream(entry));
    }

    private static byte[] readFully(InputStream stream) throws IOException {
        byte[] data = new byte[4096];
        ByteArrayOutputStream entryBuffer = new ByteArrayOutputStream();

        int len;
        do {
            len = stream.read(data);
            if (len > 0) {
                entryBuffer.write(data, 0, len);
            }
        } while(len != -1);

        return entryBuffer.toByteArray();
    }

    public static byte[] processClass(byte[] cIn, byte[] sIn, MCPMerger.ClassInfo info) {
        ClassNode cClassNode = getClassNode(cIn);
        ClassNode sClassNode = getClassNode(sIn);
        processFields(cClassNode, sClassNode, info);
        processMethods(cClassNode, sClassNode, info);
        ClassWriter writer = new ClassWriter(1);
        cClassNode.accept(writer);
        return writer.toByteArray();
    }

    private static ClassNode getClassNode(byte[] data) {
        ClassReader reader = new ClassReader(data);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        return classNode;
    }

    private static void processFields(ClassNode cClass, ClassNode sClass, MCPMerger.ClassInfo info) {
        List<FieldNode> cFields = cClass.fields;
        List<FieldNode> sFields = sClass.fields;
        int sI = 0;

        for(int x = 0; x < cFields.size(); ++x) {
            FieldNode cF = (FieldNode)cFields.get(x);
            if (sI >= sFields.size()) {
                if (cF.visibleAnnotations == null) {
                    cF.visibleAnnotations = new ArrayList();
                }

                cF.visibleAnnotations.add(getSideAnn(true));
                sFields.add(sI, cF);
                info.cField.add(cF);
            } else if (!cF.name.equals(((FieldNode)sFields.get(sI)).name)) {
                boolean serverHas = false;

                for(int y = sI + 1; y < sFields.size(); ++y) {
                    if (cF.name.equals(((FieldNode)sFields.get(y)).name)) {
                        serverHas = true;
                        break;
                    }
                }

                if (!serverHas) {
                    if (cF.visibleAnnotations == null) {
                        cF.visibleAnnotations = new ArrayList();
                    }

                    cF.visibleAnnotations.add(getSideAnn(true));
                    sFields.add(sI, cF);
                    info.cField.add(cF);
                } else {
                    boolean clientHas = false;
                    FieldNode sF = (FieldNode)sFields.get(sI);

                    for(int y = x + 1; y < cFields.size(); ++y) {
                        if (sF.name.equals(((FieldNode)cFields.get(y)).name)) {
                            clientHas = true;
                            break;
                        }
                    }

                    if (!clientHas) {
                        if (sF.visibleAnnotations == null) {
                            sF.visibleAnnotations = new ArrayList();
                        }

                        sF.visibleAnnotations.add(getSideAnn(false));
                        cFields.add(x++, sF);
                        info.sField.add(sF);
                    }
                }
            }

            ++sI;
        }

        if (sFields.size() != cFields.size()) {
            for(int x = cFields.size(); x < sFields.size(); ++x) {
                FieldNode sF = (FieldNode)sFields.get(x);
                if (sF.visibleAnnotations == null) {
                    sF.visibleAnnotations = new ArrayList();
                }

                sF.visibleAnnotations.add(getSideAnn(true));
                cFields.add(x++, sF);
                info.sField.add(sF);
            }
        }

    }

    private static void processMethods(ClassNode cClass, ClassNode sClass, MCPMerger.ClassInfo info) {
        List<MethodNode> cMethods = cClass.methods;
        List<MethodNode> sMethods = sClass.methods;
        LinkedHashSet<MCPMerger.MethodWrapper> allMethods = Sets.newLinkedHashSet();
        int cPos = 0;
        int sPos = 0;
        int cLen = cMethods.size();
        int sLen = sMethods.size();
        String clientName = "";
        String lastName = clientName;
        String serverName = "";

        while(cPos < cLen || sPos < sLen) {
            while(sPos < sLen) {
                MethodNode sM = (MethodNode)sMethods.get(sPos);
                serverName = sM.name;
                if (!serverName.equals(lastName) && cPos != cLen) {
                    break;
                }

                MCPMerger.MethodWrapper mw = new MCPMerger.MethodWrapper(sM);
                mw.server = true;
                allMethods.add(mw);
                if (++sPos >= sLen) {
                    break;
                }
            }

            while(cPos < cLen) {
                MethodNode cM = (MethodNode)cMethods.get(cPos);
                lastName = clientName;
                clientName = cM.name;
                if (!clientName.equals(lastName) && sPos != sLen) {
                    break;
                }

                MCPMerger.MethodWrapper mw = new MCPMerger.MethodWrapper(cM);
                mw.client = true;
                allMethods.add(mw);
                if (++cPos >= cLen) {
                    break;
                }
            }
        }

        cMethods.clear();
        sMethods.clear();

        for(MCPMerger.MethodWrapper mw : allMethods) {
            cMethods.add(mw.node);
            sMethods.add(mw.node);
            if (!mw.server || !mw.client) {
                if (mw.node.visibleAnnotations == null) {
                    mw.node.visibleAnnotations = Lists.newArrayListWithExpectedSize(1);
                }

                mw.node.visibleAnnotations.add(getSideAnn(mw.client));
                if (mw.client) {
                    info.sMethods.add(mw.node);
                } else {
                    info.cMethods.add(mw.node);
                }
            }
        }

    }

    public static byte[] getClassBytes(String name) throws IOException {
        InputStream classStream = null;

        byte[] var2;
        try {
            classStream = MCPMerger.class.getResourceAsStream("/" + name.replace('.', '/').concat(".class"));
            var2 = readFully(classStream);
        } finally {
            if (classStream != null) {
                try {
                    classStream.close();
                } catch (IOException var9) {
                }
            }

        }

        return var2;
    }

    private static class ClassInfo {
        public String name;
        public ArrayList<FieldNode> cField = new ArrayList();
        public ArrayList<FieldNode> sField = new ArrayList();
        public ArrayList<MethodNode> cMethods = new ArrayList();
        public ArrayList<MethodNode> sMethods = new ArrayList();

        public ClassInfo(String name) {
            this.name = name;
        }

        public boolean isSame() {
            return this.cField.size() == 0 && this.sField.size() == 0 && this.cMethods.size() == 0 && this.sMethods.size() == 0;
        }
    }

    private static class MethodWrapper {
        private MethodNode node;
        public boolean client;
        public boolean server;

        public MethodWrapper(MethodNode node) {
            this.node = node;
        }

        public boolean equals(Object obj) {
            if (obj != null && obj instanceof MCPMerger.MethodWrapper) {
                MCPMerger.MethodWrapper mw = (MCPMerger.MethodWrapper)obj;
                boolean eq = Objects.equal(this.node.name, mw.node.name) && Objects.equal(this.node.desc, mw.node.desc);
                if (eq) {
                    mw.client |= this.client;
                    mw.server |= this.server;
                    this.client |= mw.client;
                    this.server |= mw.server;
                }

                return eq;
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hashCode(new Object[]{this.node.name, this.node.desc});
        }

        public String toString() {
            return Objects.toStringHelper(this)
                    .add("name", this.node.name)
                    .add("desc", this.node.desc)
                    .add("server", this.server)
                    .add("client", this.client)
                    .toString();
        }
    }
}
