/**
 * Classe que representa uma carta do jogo Uno.
 */
public class Carta {
    public enum Cor {VERMELHO, AZUL, VERDE, AMARELO, PRETO}
    public enum Tipo {NUMERO, BLOQUEAR, INVERTER, MAIS_DOIS, MAIS_QUATRO, MUDA_COR}
    
    public Cor cor;
    public Tipo tipo;
    int valor_numerico;
    
    /**
     * Construtor para objetos da classe Carta
     */
    public Carta(Cor cor, Tipo tipo, int valor_numerico) {
        this.cor = cor;
        this.tipo = tipo;
        this.valor_numerico = valor_numerico;
    }

    // ISSO AQUI RESOLVE O SEU PROBLEMA DE IMPRESSÃO!
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