import java.util.LinkedList;
import java.util.List;

/**
 * Classe que representa um Jogador e sua respectiva mão de cartas.
 * * @author Ana Paula Barros de Jesus
 * @author Julie Quaglio da Silva Gordo
 * @author Pedro Cione Barbosa
 * @author Vitor Seiji Colombo Nishida
 */
public class Jogador {
    
    /** Nome de exibição do jogador. */
    private String nome;
    
    /** Lista que armazena as cartas atuais na mão do jogador. */
    List<Carta> maoJogador = new LinkedList<>();
    
    /**
     * Construtor da classe Jogador.
     * * @param nome O nome do jogador.
     */
    public Jogador(String nome){
        this.nome = nome;
    }
    
    /**
     * Obtém o nome do jogador.
     * * @return O nome do jogador.
     */
    public String getNome(){
        return this.nome;
    }
    
    /**
     * Adiciona uma nova carta à mão do jogador.
     * * @param A A carta a ser adicionada.
     */
    public void adicionarCarta(Carta A){
        maoJogador.add(A);
    }
    
    /**
     * Remove uma carta específica da mão do jogador.
     * * @param A A carta a ser removida.
     */
    public void removerCarta(Carta A){
        maoJogador.remove(A);
    }

    /**
     * Retorna a lista completa de cartas do jogador.
     * Útil para a Mesa checar as regras e validar jogadas.
     * * @return A lista de cartas na mão do jogador.
     */
    public List<Carta> getMao() {
        return this.maoJogador;
    }

    /**
     * Retorna a quantidade de cartas que o jogador possui.
     * Usado para verificar a condição de vitória ou aviso de "UNO!".
     * * @return O número de cartas na mão do jogador.
     */
    public int getQuantidadeCartas() {
        return this.maoJogador.size();
    }

    /**
     * Imprime as cartas da mão do jogador de forma formatada no terminal.
     */
    public void imprimirMao() {
        for (int i = 0; i < maoJogador.size(); i++) {
            System.out.print(i + ":" + maoJogador.get(i).toString() + "  ");
        }
        System.out.println();
    }
    
    /**
     * Retorna a representação em texto do jogador (seu nome).
     * * @return O nome do jogador.
     */
    @Override
    public String toString() {
        return this.nome;
    }
}