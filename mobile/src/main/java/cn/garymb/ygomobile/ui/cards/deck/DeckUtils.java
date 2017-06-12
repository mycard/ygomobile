package cn.garymb.ygomobile.ui.cards.deck;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cn.garymb.ygomobile.bean.Deck;
import cn.garymb.ygomobile.utils.IOUtils;

public class DeckUtils {
    public static boolean save(Deck deck, File file) {
        if (deck == null) return false;
        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            if (file == null) {
                return false;
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            writer = new OutputStreamWriter(outputStream, "utf-8");
            writer.write("#created by ygomobile".toCharArray());
            writer.write("\n#main".toCharArray());
            for(long id :deck.getMainlist()){
                writer.write(("\n" + id).toCharArray());
            }
            writer.write("\n#extra".toCharArray());
            for(long id :deck.getExtraList()){
                writer.write(("\n" + id).toCharArray());
            }
            writer.write("\n!side".toCharArray());
            for(long id :deck.getSideList()){
                writer.write(("\n" + id).toCharArray());
            }
            writer.flush();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.close(writer);
            IOUtils.close(outputStream);
        }
        return true;
    }
}
