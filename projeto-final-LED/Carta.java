/**
 * Classe que representa uma carta do jogo Uno.
 * * @author Ana Paula Barros de Jesus
 * @author Julie Quaglio da Silva Gordo
 * @author Pedro Cione Barbosa
 * @author Vitor Seiji Colombo Nishida
 */
public class Carta {
    
    /**
     * Enumeração que define as cores possíveis para uma carta de Uno.
     * PRETO é utilizado para cartas coringa (Muda Cor e +4).
     */
    public enum Cor {VERMELHO, AZUL, VERDE, AMARELO, PRETO}
    
    /**
     * Enumeração que define os tipos de cartas disponíveis no jogo.
     */
    public enum Tipo {NUMERO, BLOQUEAR, INVERTER, MAIS_DOIS, MAIS_QUATRO, MUDA_COR}
    
    /** A cor atual da carta. */
    public Cor cor;
    
    /** O tipo da carta (ação, número ou coringa). */
    public Tipo tipo;
    
    /** O valor numérico da carta (de 0 a 9). Cartas de ação possuem valor -1. */
    int valor_numerico;
    
    /**
     * Construtor para objetos da classe Carta.
     * * @param cor A cor atribuída à carta.
     * @param tipo O tipo de carta (número ou ação).
     * @param valor_numerico O valor da carta caso seja do tipo NUMERO.
     */
    public Carta(Cor cor, Tipo tipo, int valor_numerico) {
        this.cor = cor;
        this.tipo = tipo;
        this.valor_numerico = valor_numerico;
    }

    /**
     * Retorna uma representação em texto da carta para exibição no terminal.
     * * @return Uma string formatada no padrão [COR NUMERO], [TIPO] ou [COR TIPO].
     */
    @Override
    public String toString() {
        if (tipo == Tipo.NUMERO) {
            return "[" + cor + " " + valor_numerico + "]";
        } else if (cor == Cor.PRETO) {
            return "[" + tipo + "]"; // Coringas
        } else {
            return "[" + cor + " " + tipo + "]"; // Cartas de ação (+2, Bloquear, Inverter)
        }
    }
}