
package projetoContatos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class Categorias extends JDialog{
    
    private DefaultListModel<String> modelo;
    private JList<String> listaCategorias;
    private JScrollPane scrollPane;
    private JButton btAdicionar, btExcluir;
    private JTextField tfAdicionar;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ArrayList<Categoria> categorias = new ArrayList<>();
    
    public Categorias(Adicionar adicionar,Runnable onCloseCallback){
        super(adicionar,"Categorias",true);
        setLayout(null);
        setResizable(false);
        
        //List
        modelo = new DefaultListModel<>();
        listaCategorias = new JList<>(modelo);
        listaCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(listaCategorias);
        scrollPane.setBounds(8,31,180,200);
        if(verificarArquivo("categorias.json")){
            carregarCategoria();
        }
        add(scrollPane);
        
        //buttons
        btAdicionar = new JButton("Adicionar");
        btExcluir = new JButton("Excluir");
        btAdicionar.setBounds(8,10,90,20);
        btExcluir.setBounds(8,231,180,20);

        tfAdicionar = new JTextField(100);
        tfAdicionar.setBounds(98,10,90,21);
        add(tfAdicionar);

        btAdicionar.addActionListener((ActionEvent e) -> {
            if(!tfAdicionar.getText().isEmpty() && tfAdicionar.getText().length() <= 100){
                Categoria categoria = new Categoria();
                categoria.setNome(tfAdicionar.getText());
                salvarCategoria(categoria);
                carregarCategoria();
                tfAdicionar.setText("");
            }
        });
        
        btExcluir.addActionListener((ActionEvent e) -> {
            excluirCategoria();
        });

        add(btAdicionar);
        add(btExcluir);
        
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
        
        setSize(210,300);
        setLocationRelativeTo(null);
    }
    
    
    public void salvarCategoria(Categoria categoria) {
        try (BufferedReader reader = new BufferedReader(new FileReader("categorias.json"))) {
            categorias = gson.fromJson(reader, new TypeToken<ArrayList<Categoria>>(){}.getType());
            if (categorias == null) {
                categorias = new ArrayList<>();
            }    
        } catch (IOException e) {
            System.out.println("Arquivo não encontrado. Criando novo arquivo.");
        } 
        
        categorias.add(categoria);
        try (FileWriter writer = new FileWriter("categorias.json")) {
            gson.toJson(categorias, writer);
        } catch (IOException e) {
        }
    }
    
    
    public void carregarCategoria(){
        try (BufferedReader reader = new BufferedReader(new FileReader("categorias.json"))) {
            categorias = gson.fromJson(reader, new TypeToken<ArrayList<Categoria>>(){}.getType());
            if (categorias == null) {
                categorias = new ArrayList<>();
            }
            modelo.clear();
            for (Categoria categoria : categorias) {
                modelo.addElement(categoria.getNome());
            }
        } catch (IOException e) {
        }
    }
    
    
    public void excluirCategoria(){
        int selecionado = listaCategorias.getSelectedIndex();
        if (selecionado != -1) { 
            String categoriaNome = modelo.getElementAt(selecionado);
            for(Categoria categoria:categorias){
                if(categoria.getNome().equals(categoriaNome)){
                    categorias.remove(categoria);
                    break;
                }
            }
            modelo.remove(selecionado);

            try (FileWriter writer = new FileWriter("categorias.json")) {
                gson.toJson(categorias, writer);
            } catch (IOException e) {
            }
        }
    }
    
    
    public boolean verificarArquivo(String arquivo) {
        File file = new File(arquivo);
        return file.exists();
    }
}
