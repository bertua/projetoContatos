
package projetoContatos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.MaskFormatter;


public class Adicionar extends JDialog{
    
    private JLabel lbNome,lbTelefone,lbEmail,lbEndereco,lbCategoria;
    private JTextField tfNome,tfEmail,tfEndereco;
    private JFormattedTextField tfTelefone;
    private JButton btSalvar, btCancelar, btAdCategoria, btSelecionarArq;
    private JComboBox cbCategoria;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ArrayList<Contato> contatos = new ArrayList<>();
    private ArrayList<Categoria> categorias = new ArrayList<>();
    private String contatosFilePath = "";
    
    
    public Adicionar(Contatos contatos,Runnable onCloseCallbackCont, Runnable onCloseCallbackCateg){
        super(contatos, "Adicionar Contato", true);
        setLayout(null);
        setResizable(false);
        
        //Labels
        lbNome = new JLabel("Nome");
        lbTelefone = new JLabel("Telefone");
        lbEmail = new JLabel("E-mail");
        lbEndereco = new JLabel("Endereço");
        lbCategoria = new JLabel("Categoria");
        lbNome.setBounds(50,20,120,20);
        lbTelefone.setBounds(50,60,120,20);
        lbEmail.setBounds(50,100,120,20);
        lbEndereco.setBounds(50,140,120,20);
        lbCategoria.setBounds(50,180,120,20);
        add(lbNome);
        add(lbTelefone);
        add(lbEmail);
        add(lbEndereco);
        add(lbCategoria);
        
        
        //Areas de preenchimento
        MaskFormatter mascaraTel = null;
        try{
            mascaraTel = new MaskFormatter("(##)#####-####");
        } catch(ParseException e) {
            System.err.println("Erro na formatação: " + e.getMessage());
            System.exit(-1);
        }
        
        tfNome = new JTextField(100);
        tfTelefone = new JFormattedTextField(mascaraTel);
        tfEmail = new JTextField(100);
        tfEndereco = new JTextField(100);
        cbCategoria = new JComboBox();
        if(verificarArquivo("categorias.json")){
            carregarCategoria();
        }
        cbCategoria.setSelectedItem(null);
        tfNome.setBounds(150,20,200,20);
        tfTelefone.setBounds(150,60,200,20);
        tfEmail.setBounds(150,100,200,20);
        tfEndereco.setBounds(150,140,200,20);
        cbCategoria.setBounds(150,180,150,20);
        add(tfNome);
        add(tfTelefone);
        add(tfEmail);
        add(tfEndereco);
        add(cbCategoria);
        
        
        //Botões
        btSalvar = new JButton("Salvar");
        btCancelar = new JButton("Cancelar");
        btAdCategoria = new JButton("+");
        btSelecionarArq = new JButton("Selecionar arquivo");
        btSalvar.setBounds(150,210,100,20);
        btCancelar.setBounds(250,210,100,20);
        btAdCategoria.setBounds(300,180,50,20);
        btSelecionarArq.setBounds(150,230,200,20);
        btAdCategoria.setToolTipText("Alterar categorias");
        
        
        btAdCategoria.addActionListener((ActionEvent e) -> {
            Categorias categorias = new Categorias(this,this::carregarCategoria);
            categorias.setVisible(true);
        });
        
        
        btSalvar.addActionListener((ActionEvent e) -> {
            boolean error = false;
            if(tfNome.getText().isEmpty() || tfNome.getText().length() > 200){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o nome (máx. 200 caracteres)");
            }
            if(!tfTelefone.getText().matches("\\(\\d{2}\\)\\d{5}-\\d{4}")){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o telefone");
            }
            if(!tfEmail.getText().matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$") || tfEmail.getText().length() > 200){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o email (máx. 200 caracteres)");
            }
            if(tfEndereco.getText().isEmpty() || tfEndereco.getText().length() > 200){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o endereço (máx. 200 caracteres)");
            }
            if(cbCategoria.getSelectedItem() == null){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Escolha a categoria");
            }
            if(verificarArquivo("path.txt")){
                contatosFilePath = pegarPath();
            } else{
                JOptionPane.showMessageDialog(rootPane, "Escolha o arquivo de destino");
            }
            if(error == false && verificarArquivo("path.txt")){
                Contato contato = new Contato();
                contato.setNome(tfNome.getText());
                contato.setTelefone(tfTelefone.getText());
                contato.setEmail(tfEmail.getText());
                contato.setEndereco(tfEndereco.getText());
                contato.setCategoria(cbCategoria.getSelectedItem().toString());
                salvarContato(contato);
                tfNome.setText("");
                tfTelefone.setText("");
                tfEmail.setText("");
                tfEndereco.setText("");
                cbCategoria.setSelectedItem(null);
            }
        });
        
        
        btCancelar.addActionListener((ActionEvent e) -> {
            this.dispose();
        });
        
        
        btSelecionarArq.addActionListener((ActionEvent e) -> {
            escolherArquivo();
        });
        
        add(btSalvar);
        add(btCancelar);
        add(btAdCategoria);
        add(btSelecionarArq);
        
        //
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (onCloseCallbackCont != null && onCloseCallbackCateg != null) {
                    onCloseCallbackCont.run();
                    onCloseCallbackCateg.run();
                }
                dispose();
            }
            
        });
        
        setSize(400,300);
        setLocationRelativeTo(null);
    }
    
    
    public void salvarContato(Contato contato) {
        try (BufferedReader reader = new BufferedReader(new FileReader(contatosFilePath))) {
            contatos = gson.fromJson(reader, new TypeToken<ArrayList<Contato>>(){}.getType());
            if (contatos == null) {
                contatos = new ArrayList<>();
            }    
        } catch (IOException e) {
            System.out.println("Arquivo não encontrado. Criando novo arquivo.");
        } 
        
        contatos.add(contato);
        try (FileWriter writer = new FileWriter(contatosFilePath)) {
            gson.toJson(contatos, writer);
            JOptionPane.showMessageDialog(this, "Contato salvo!");
        } catch (IOException e) {
        }
    }
    
    
    public void carregarCategoria(){
        try (BufferedReader reader = new BufferedReader(new FileReader("categorias.json"))) {
            categorias = gson.fromJson(reader, new TypeToken<ArrayList<Categoria>>(){}.getType());
            if (categorias == null) {
                categorias = new ArrayList<>();
            }
            cbCategoria.removeAllItems();
            for (Categoria categoria : categorias) {
                cbCategoria.addItem(categoria.getNome());
            }
            cbCategoria.setSelectedItem(null);
        } catch (IOException e) {
        }
    }
    
    
    public String pegarPath(){
        String linha = "";
        try (BufferedReader reader = new BufferedReader(new FileReader("path.txt"))) {
            String i;
            while ((i = reader.readLine()) != null) {
                linha = i;
            }
        } catch (IOException e) {
        }
        return linha;
    }
    
    
    public boolean verificarArquivo(String arquivo) {
        File file = new File(arquivo);
        return file.exists();
    }
    
    
    public void escolherArquivo(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecionar arquivo de contatos");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            contatosFilePath = fileToOpen.getAbsolutePath();
            JOptionPane.showMessageDialog(this, "Arquivo selecionado: " + contatosFilePath);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("path.txt"))) {
                writer.write(contatosFilePath);
            } catch (IOException e) {
            }
        }
    }
}
