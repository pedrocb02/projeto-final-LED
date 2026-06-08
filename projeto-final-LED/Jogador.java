import java.util.LinkedList;
import java.util.List;

/**
 * Classe que representa um Jogador e sua respectiva mão de cartas.
 * * @author Ana Paula Barros de Jesus
 * *         Julie Quaglio da Silva Gordo
 * *         Pedro Cione Barbosa
 * *         Vitor Seiji Colombo Nishida
 * 
 */

public class Jogador {
    private String nome;
    List<Carta> maoJogador = new LinkedList<>();
    
    public Jogador(String nome){
        this.nome = nome;
    }
    
    public String getNome(){
        return this.nome;
    }
    
    public void adicionarCarta(Carta A){
        maoJogador.add(A);
    }
    
    public void removerCarta(Carta A){
        maoJogador.remove(A);
    }

    // Retorna a lista de cartas para a Mesa poder checar as regras
    public List<Carta> getMao() {
        return this.maoJogador;
    }

    // Retorna quantas cartas o jogador tem (para saber se ele ganhou)
    public int getQuantidadeCartas() {
        return this.maoJogador.size();
    }

    // Imprime as cartas bonitinhas no terminal
    public void imprimirMao() {
        for (int i = 0; i < maoJogador.size(); i++) {
            // Como agora a Carta tem o toString(), isso vai imprimir [COR NUMERO]
            System.out.print(i + ":" + maoJogador.get(i).toString() + "  ");
        }
        System.out.println();
    }
    
    @Override
    public String toString() {
        return this.nome;
    }
}