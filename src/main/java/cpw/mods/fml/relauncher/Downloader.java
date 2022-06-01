package cpw.mods.fml.relauncher;

import cpw.mods.fml.common.FMLLog;
import java.awt.Dimension;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class Downloader extends JOptionPane implements IDownloadDisplay {
    private JDialog container;
    private JLabel currentActivity;
    private JProgressBar progress;
    boolean stopIt;
    Thread pokeThread;

    public Downloader() {
    }

    private Box makeProgressPanel() {
        Box box = Box.createVerticalBox();
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel welcomeLabel = new JLabel("<html><b><font size='+1'>FML is setting up your minecraft environment</font></b></html>");
        box.add(welcomeLabel);
        welcomeLabel.setAlignmentY(0.0F);
        welcomeLabel = new JLabel("<html>Please wait, FML has some tasks to do before you can play</html>");
        welcomeLabel.setAlignmentY(0.0F);
        box.add(welcomeLabel);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        this.currentActivity = new JLabel("Currently doing ...");
        box.add(this.currentActivity);
        box.add(Box.createRigidArea(new Dimension(0, 10)));
        this.progress = new JProgressBar(0, 100);
        this.progress.setStringPainted(true);
        box.add(this.progress);
        box.add(Box.createRigidArea(new Dimension(0, 30)));
        return box;
    }

    public JDialog makeDialog() {
        this.setMessageType(1);
        this.setMessage(this.makeProgressPanel());
        this.setOptions(new Object[]{"Stop"});
        this.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() == Downloader.this && evt.getPropertyName() == "value") {
                    Downloader.this.requestClose("This will stop minecraft from launching\nAre you sure you want to do this?");
                }

            }
        });
        this.container = new JDialog(null, "Hello", ModalityType.MODELESS);
        this.container.setResizable(false);
        this.container.setLocationRelativeTo(null);
        this.container.add(this);
        this.updateUI();
        this.container.pack();
        this.container.setMinimumSize(this.container.getPreferredSize());
        this.container.setVisible(true);
        this.container.setDefaultCloseOperation(0);
        this.container.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Downloader.this.requestClose("Closing this window will stop minecraft from launching\nAre you sure you wish to do this?");
            }
        });
        return this.container;
    }

    protected void requestClose(String message) {
        int shouldClose = JOptionPane.showConfirmDialog(this.container, message, "Are you sure you want to stop?", 0, 2);
        if (shouldClose == 0) {
            this.container.dispose();
        }

        this.stopIt = true;
        if (this.pokeThread != null) {
            this.pokeThread.interrupt();
        }

    }

    public void updateProgressString(String progressUpdate, Object... data) {
        FMLLog.finest(progressUpdate, data);
        if (this.currentActivity != null) {
            this.currentActivity.setText(String.format(progressUpdate, data));
        }

    }

    public void resetProgress(int sizeGuess) {
        if (this.progress != null) {
            this.progress.getModel().setRangeProperties(0, 0, 0, sizeGuess, false);
        }

    }

    public void updateProgress(int fullLength) {
        if (this.progress != null) {
            this.progress.getModel().setValue(fullLength);
        }

    }

    public void makeHeadless() {
        this.container = null;
        this.progress = null;
        this.currentActivity = null;
    }

    public void setPokeThread(Thread currentThread) {
        this.pokeThread = currentThread;
    }

    public boolean shouldStopIt() {
        return this.stopIt;
    }
}
