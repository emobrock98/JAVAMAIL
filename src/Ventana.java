import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

public class Ventana extends JFrame implements ActionListener {
    JLabel para, cc, cco, asunto, archivo;
    JButton bArchivo, enviar, eliminar;
    JTextField paraTxt, ccTxt, ccoTxt, asuntoTxt;
    JTextArea correo;
    Container container;
    Properties properties;
    Session session;
    File adjunto;

    public void initGUI() throws HeadlessException {
        instancias();
        sesionGmail();
        configurarContainer();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        acciones();
        setTitle("JavaMAIL");
        this.setSize(new Dimension(600, 400));
        setVisible(true);
    }

    public void acciones() {
        eliminar.addActionListener(this);
        enviar.addActionListener(this);
    }

    public void instancias() {
        cc = new JLabel("CC:");
        para = new JLabel("Para:");
        cco = new JLabel("CCO:");
        asunto = new JLabel("Asunto");
        archivo = new JLabel("Adjuntar");
        paraTxt = new JTextField();
        ccTxt = new JTextField();
        ccoTxt = new JTextField();
        asuntoTxt = new JTextField();
        bArchivo = new JButton("Seleccionar...");
        enviar = new JButton("Enviar");
        eliminar = new JButton("Eliminar");
        correo = new JTextArea("Introduzca mensaje");
        properties = new Properties();
        session = Session.getInstance(properties, null);
        container = getContentPane();
    }

    public void configurarConstraint(int posX, int posY, int fill, int anchor, double pesX, double pesY, int tamX, int tamY, JComponent component) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = posX;
        constraints.gridy = posY;
        constraints.fill = fill;
        constraints.anchor = anchor;
        constraints.weightx = pesX;
        constraints.weighty = pesY;
        constraints.gridwidth = tamX;
        constraints.gridheight = tamY;
        constraints.insets = new Insets(5, 5, 5, 5);
        container.add(component, constraints);
    }

    public void configurarContainer() {
        container.setLayout(new GridBagLayout());
        //fila 1
        configurarConstraint(0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, 0, 1, 1, para);
        configurarConstraint(1, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0.2, 0, 3, 1, paraTxt);

        //fila 2
        configurarConstraint(0, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, 0, 1, 1, cc);
        configurarConstraint(1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0.2, 0, 1, 1, ccTxt);
        configurarConstraint(2, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, 0, 1, 1, cco);
        configurarConstraint(3, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST, 1, 0, 1, 1, ccoTxt);
        //fila 3
        configurarConstraint(0, 2, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, 0, 1, 1, asunto);
        configurarConstraint(1, 2, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0.8, 0, 3, 1, asuntoTxt);
        //fila 4
        configurarConstraint(0, 3, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, 0, 1, 1, archivo);
        configurarConstraint(1, 3, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0.2, 0, 1, 1, bArchivo);
        //fila 5
        configurarConstraint(0, 4, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1, 4, 2, correo);
        //fila 6
        configurarConstraint(1, 6, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0.2, 0, 1, 1, enviar);
        configurarConstraint(2, 6, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0.2, 0, 1, 1, eliminar);
    }

    public void sesionGmail() {
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.user", "eddymartinobiang@gmail.com");
        properties.put("mail.password", "");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == eliminar) {
            correo.setText("");
            paraTxt.setText("");
            ccTxt.setText("");
            ccoTxt.setText("");
            asuntoTxt.setText("");
        } else if(e.getSource() == bArchivo){
            JFileChooser fileDialog = new JFileChooser();
            int opcion = fileDialog.showDialog(this,"Abrir");
            switch (opcion){
                case JFileChooser.APPROVE_OPTION:
                    adjunto = fileDialog.getSelectedFile();
                    archivo.setText(adjunto.getAbsolutePath());
                    break;}
        }
        else if (e.getSource() == enviar) {
            sesionGmail();
            MimeMessage mimeMessage = new MimeMessage(session);
            try {
                MimeMultipart mimeMultipart = new MimeMultipart();
                MimeBodyPart mimeBodyPartTexto = new MimeBodyPart();
                mimeBodyPartTexto.setText(correo.getText().toString());
                mimeMultipart.addBodyPart(mimeBodyPartTexto);
                mimeMessage.setContent(mimeMultipart);
                mimeMessage.setFrom(new InternetAddress(properties.getProperty("mail.user")));
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(paraTxt.getText()));
                mimeMessage.setRecipient(Message.RecipientType.CC,new InternetAddress(ccTxt.getText()));
                mimeMessage.setRecipient(Message.RecipientType.BCC,new InternetAddress(ccoTxt.getText()));
                mimeMessage.setSubject(asuntoTxt.getText());
                Transport transport = session.getTransport("smtp");
                transport.connect(properties.getProperty("mail.user"), properties.getProperty("mail.password"));
                transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                transport.close();
            } catch (MessagingException e1) {
                e1.printStackTrace();
            }
        }
    }
}