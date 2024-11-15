
package projetoContatos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.text.MaskFormatter;


public class Editar extends JDialog{
    
    private JLabel lbNome,lbTelefone,lbEmail,lbEndereco,lbCategoria;
    private JTextField tfNome,tfEmail,tfEndereco;
    private JFormattedTextField tfTelefone;
    private JButton btSalvar, btCancelar;
    private JComboBox cbCategoria;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ArrayList<Contato> contatos = new ArrayList<>();
    private ArrayList<Categoria> categorias = new ArrayList<>();
    private String contatosFilePath = "";
    
    
    public Editar(Contatos contatos, Contato contatoSelecionado, String path, Runnable onCloseCallback){
        super(contatos, "Editar Contato", true);
        setLayout(null);
        setResizable(false);
        contatosFilePath = path;
        
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
        tfNome.setBounds(150,20,200,20);
        tfTelefone.setBounds(150,60,200,20);
        tfEmail.setBounds(150,100,200,20);
        tfEndereco.setBounds(150,140,200,20);
        cbCategoria.setBounds(150,180,200,20);
        tfNome.setText(contatoSelecionado.getNome());
        tfTelefone.setText(contatoSelecionado.getTelefone());
        tfEmail.setText(contatoSelecionado.getEmail());
        tfEndereco.setText(contatoSelecionado.getEndereco());
        cbCategoria.setSelectedItem(contatoSelecionado.getCategoria());
        add(tfNome);
        add(tfTelefone);
        add(tfEmail);
        add(tfEndereco);
        add(cbCategoria);
        
        
        //Botões
        btSalvar = new JButton("Salvar");
        btCancelar = new JButton("Cancelar");
        btSalvar.setBounds(150,220,100,20);
        btCancelar.setBounds(250,220,100,20);
     
        btSalvar.addActionListener((ActionEvent e) -> {
            boolean error = false;
            if(tfNome.getText().isEmpty() || tfNome.getText().length() > 200){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o nome (máx. 200 caractéres)");
            }
            if(!tfTelefone.getText().matches("\\(\\d{2}\\)\\d{5}-\\d{4}")){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o telefone");
            }
            if(!tfEmail.getText().matches("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$") || tfEmail.getText().length() > 200){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o email (máx. 200 caractéres)");
            }
            if(tfEndereco.getText().isEmpty() || tfEndereco.getText().length() > 200){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Preencha o endereço (máx. 200 caractéres)");
            }
            if(cbCategoria.getSelectedItem() == null){
                error = true;
                JOptionPane.showMessageDialog(rootPane, "Escolha a categoria");
            }
            if(error == false){
                Contato contato = new Contato();
                contato.setNome(tfNome.getText());
                contato.setTelefone(tfTelefone.getText());
                contato.setEmail(tfEmail.getText());
                contato.setEndereco(tfEndereco.getText());
                contato.setCategoria(cbCategoria.getSelectedItem().toString());
                salvarContato(contato,contatoSelecionado);
            }
        });
        
        btCancelar.addActionListener((ActionEvent e) -> {
            this.dispose();
        });
        
        add(btSalvar);
        add(btCancelar);

        
        //
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
                dispose();
            }
        });
        
        setSize(400,300);
        setLocationRelativeTo(null);
    }
    
    public boolean verificarArquivo(String arquivo) {
        File file = new File(arquivo);
        return file.exists();
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
            e.printStackTrace();
        }
    }
    
    public void salvarContato(Contato contato, Contato contatoSelecionado) {
        try (BufferedReader reader = new BufferedReader(new FileReader(contatosFilePath))) {
            contatos = gson.fromJson(reader, new TypeToken<ArrayList<Contato>>(){}.getType());
            if (contatos == null) {
                contatos = new ArrayList<>();
            }    
        } catch (IOException e) {
            System.out.println("Arquivo não encontrado. Criando novo arquivo.");
        } 
        
        Contato excluido = new Contato();
        for(Contato c:contatos){
            if(c.getNome().equals(contatoSelecionado.getNome()) && c.getTelefone().equals(contatoSelecionado.getTelefone()) && c.getEmail().equals(contatoSelecionado.getEmail())){
                excluido = c;
            }
        }
        
        contatos.remove(excluido);
        contatos.add(contato);
        try (FileWriter writer = new FileWriter(contatosFilePath)) {
            gson.toJson(contatos, writer);
            JOptionPane.showMessageDialog(this, "Contato salvo!");
        } catch (IOException e) {
        }
    }
}
