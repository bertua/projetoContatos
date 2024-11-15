
package projetoContatos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;


public class Contatos extends JFrame{
    
    private Container tela;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPopupMenu popupMenu;
    private DefaultTableModel modelo;
    private JTable tabela;
    private JScrollPane scrollPane;
    private JPanel panel;
    private JTextField txtPesquisa;
    private JButton btPesquisar;
    private JComboBox<String> cbCategoria;
    private ArrayList<Contato> contatos = new ArrayList<>();
    private ArrayList<Categoria> categorias = new ArrayList<>();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String contatosFilePath = "";
    

    public Contatos(){
        super("Contatos");
        tela = getContentPane();
        setLayout(null);
        setResizable(false);
        
        //LookAndFeel
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        //MenuBar
        menuBar = new JMenuBar();
        menuBar.setBounds(0,0,600,30);
        
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem menuCarregar = new JMenuItem("Carregar");
        JMenuItem menuSair = new JMenuItem("Sair");
        menuArquivo.add(menuCarregar);
        menuArquivo.add(menuSair);
        
        JMenu menuEditar = new JMenu("Editar");
        JMenuItem menuAdicionar = new JMenuItem("Adicionar");
        JMenuItem menuEditar2 = new JMenuItem("Editar");
        JMenuItem menuExcluir = new JMenuItem("Excluir");
        menuEditar.add(menuAdicionar);
        menuEditar.add(menuEditar2);
        menuEditar.add(menuExcluir);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem menuSobre = new JMenuItem("Sobre");
        menuAjuda.add(menuSobre);
        
        menuCarregar.addActionListener((ActionEvent e) -> {
            escolherArquivo();
            if(contatosFilePath.length() != 0){
                carregarContato();
            }
        });
        
        menuSair.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        
        menuAdicionar.addActionListener((ActionEvent e) -> {
            adicionarContato();
        });
        
        menuEditar2.addActionListener((ActionEvent e) -> {
            editarContato();
        });
        
        menuExcluir.addActionListener((ActionEvent e) -> {
            excluirContato();
        });
        
        menuSobre.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(rootPane, "appContatos Version 1.0");
        });
        
        menuBar.add(menuArquivo);
        menuBar.add(menuEditar);
        menuBar.add(menuAjuda);
        tela.add(menuBar);


        //ToolBar
        toolBar = new JToolBar();
        toolBar.setBounds(0,30,585,30);
        toolBar.setBorder(null);
        
        JMenuItem toolAdicionar = new JMenuItem("Adicionar");
        JMenuItem toolEditar = new JMenuItem("Editar");
        JMenuItem toolExcluir = new JMenuItem("Excluir");
        JMenuItem toolCarregar = new JMenuItem("Carregar");
        toolBar.add(toolAdicionar);
        toolBar.add(toolEditar);
        toolBar.add(toolExcluir);
        toolBar.add(toolCarregar);
        
        toolAdicionar.addActionListener((ActionEvent e) -> {
            adicionarContato();
        });
        
        toolEditar.addActionListener((ActionEvent e) -> {
            editarContato();
        });
        
        toolExcluir.addActionListener((ActionEvent e) -> {
            excluirContato();
        });
        
        toolCarregar.addActionListener((ActionEvent e) -> {
            escolherArquivo();
            if(contatosFilePath.length() != 0){
                carregarContato();
            }
        });
        tela.add(toolBar);
        
        
        //Tabela
        String[] colunas = {"Nome","Telefone","E-mail","Endereço","Categoria"};
        modelo = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modelo);
        scrollPane = new JScrollPane(tabela);
        scrollPane.setBounds(0,60,585,400);
        tela.add(scrollPane);
        
        
        //PopupMenu
        popupMenu = new JPopupMenu();
        JMenuItem popupEditar = new JMenuItem("Editar");
        JMenuItem popupExcluir = new JMenuItem("Excluir");
        popupMenu.add(popupEditar);
        popupMenu.add(popupExcluir);
        
        popupEditar.addActionListener((ActionEvent e) -> {
            editarContato();
        });
        
        popupExcluir.addActionListener((ActionEvent e) -> {
            excluirContato();
        });
        
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupMenu(e);
            }

            private void showPopupMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        
        //Filtro e pesquisa
        panel = new JPanel();
        panel.setBounds(0,460,590,50);
        
        txtPesquisa = new JTextField(10);
        btPesquisar = new JButton("Pesquisar");
        panel.add(btPesquisar);
        panel.add(txtPesquisa);
        panel.add(new JLabel("                                                  "));
        
        cbCategoria = new JComboBox();
        if(verificarArquivo("categorias.json")){
            carregarCategoria();
        }
        panel.add(new JLabel("Filtrar por categoria:"));
        panel.add(cbCategoria);
        
        cbCategoria.addActionListener((ActionEvent e) -> {
            filtrar();
        });
        
        btPesquisar.addActionListener((ActionEvent e) -> {
            cbCategoria.setSelectedItem(null);
            pesquisar();
        });
        
        tela.add(panel);
        
        //
        if(verificarArquivo("path.txt")){
            contatosFilePath = pegarPath();
            carregarContato();
        }

        setSize(600,540); 
        setVisible(true);
        setLocationRelativeTo(null);
    }
    
    
    public void adicionarContato() {
        Adicionar adicionar = new Adicionar(this, this::carregarContato, this::carregarCategoria);
        adicionar.setVisible(true);
    }
    
    
    public void editarContato() {
        int selectedRow = tabela.getSelectedRow();
        if (selectedRow != -1) {
            Contato contatoSelecionado = contatos.get(selectedRow);
            Editar editar = new Editar(this, contatoSelecionado, contatosFilePath, this::carregarContato);
            editar.setVisible(true);
        }
    }
    
    
    public void excluirContato() {
        int selectedRow = tabela.getSelectedRow();
        if (selectedRow != -1) {
            int confirmar = JOptionPane.showConfirmDialog(rootPane, "Deseja excluir esse contato?", "Excluir contato", JOptionPane.YES_NO_CANCEL_OPTION);
            if(confirmar == JOptionPane.YES_OPTION){
                modelo.removeRow(selectedRow);
                contatos.remove(selectedRow);
                salvarContato();
                carregarContato();
            }
        }
    }
    
    
    public void salvarContato() {
        try (FileWriter writer = new FileWriter(contatosFilePath)) {
            gson.toJson(contatos, writer);
        } catch (IOException e) {
        }
    }
    
    
    public void carregarContato() {
        try (BufferedReader reader = new BufferedReader(new FileReader(contatosFilePath))) {
            contatos = gson.fromJson(reader, new TypeToken<ArrayList<Contato>>(){}.getType());
            if (contatos == null) {
                contatos = new ArrayList<>();
            }
            modelo.setRowCount(0);
            for (Contato contato : contatos) {
               Object[] rowData = new Object[]{
                contato.getNome(),
                contato.getTelefone(),
                contato.getEmail(),
                contato.getEndereco(),
                contato.getCategoria()
               };
               modelo.addRow(rowData);
            }
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
            Categoria c = new Categoria();
            c.setNome("Todos");
            categorias.add(0, c);
            for (Categoria categoria : categorias) {
                cbCategoria.addItem(categoria.getNome());
            }
        } catch (IOException e) {
        }
    }
    
    
    public boolean verificarArquivo(String arquivo) {
        File file = new File(arquivo);
        return file.exists();
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
    
    
    private void filtrar() {
        try {
            String selecionado = cbCategoria.getSelectedItem().toString();
            modelo.setRowCount(0);

            for (Contato contato : contatos) {
                if (contato.getCategoria().equals(selecionado) || selecionado.equals("Todos")) {
                    modelo.addRow(new Object[]{
                        contato.getNome(),
                        contato.getTelefone(),
                        contato.getEmail(),
                        contato.getEndereco(),
                        contato.getCategoria()
                    });
                }
            }
        } catch (Exception e) {
        }
    }
    
    
    public void pesquisar() {
        String pesquisa = txtPesquisa.getText().toLowerCase();
        modelo.setRowCount(0);

        for (Contato contato : contatos) {
            if (contato.getNome().toLowerCase().contains(pesquisa) ||
                contato.getTelefone().toLowerCase().contains(pesquisa) ||
                contato.getEmail().toLowerCase().contains(pesquisa) ||
                contato.getEndereco().toLowerCase().contains(pesquisa) ||
                contato.getCategoria().toLowerCase().contains(pesquisa)) {
                modelo.addRow(new Object[]{
                    contato.getNome(),
                    contato.getTelefone(),
                    contato.getEmail(),
                    contato.getEndereco(),
                    contato.getCategoria()
                });
            }
        }
    }
    
}
