package sdp.gui;

import sdp.comms.PacketListener;
import sdp.comms.Radio;
import sdp.comms.SingletonRadio;
import sdp.comms.packets.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class RecieveTest extends JDialog implements PacketListener {
    private JPanel contentPane;
    private JButton sendRequestButton;
    private JTextArea textArea1;
    private JButton buttonOK;
    private JButton buttonCancel;
    private SingletonRadio radio;

    public RecieveTest() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        String[] serialPorts = Radio.getPortNames();

        System.out.println("Please select radio port: ");
        Scanner userChoiceInput = new Scanner(System.in);
        int portNum = userChoiceInput.nextInt();
        while (!(portNum >= 0 && portNum < serialPorts.length)) {
            System.out.println("ERROR: You need to pick a number between 0 and " + serialPorts.length);
            System.out.println("Enter the number for the port you want to use: ");
            portNum = userChoiceInput.nextInt();
        }
        radio = new SingletonRadio(serialPorts[portNum]);

        radio.sendPacket(new ActivatePacket());

        radio.addListener(this);

        sendRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radio.sendPacket(new EngageCatcherPacket());
            }
        });
    }

    @Override
    public void packetArrived(Packet p) {
        if (p instanceof BallStatePacket) {
            String s = ((BallStatePacket) p).ballCaught ? "caught" : "not caught";
            textArea1.setText(s);
        }
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        RecieveTest dialog = new RecieveTest();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        sendRequestButton = new JButton();
        sendRequestButton.setText("Send Request");
        contentPane.add(sendRequestButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textArea1 = new JTextArea();
        contentPane.add(textArea1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
