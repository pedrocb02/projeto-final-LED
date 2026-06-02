
/**
 * Escreva uma descrição da classe Carta aqui.
 * 
 * @author (seu nome) 
 * @version (um número da versão ou uma data)
 */
public class Carta
{
    // variáveis de instância - substitua o exemplo abaixo pelo seu próprio
    public enum Cor {VERMELHO, AZUL, VERDE, AMARELO, PRETO}
    public enum Tipo {NUMERO, BLOQUEAR, INVERTER, MAIS_DOIS, MAIS_QUATRO, MUDA_COR}
    public Cor cor;
    public Tipo tipo;
    int valor_numerico;
    /**
     * Construtor para objetos da classe Carta
     */
    public Carta(Cor cor, Tipo tipo, int valor_numerico)
    {
        // inicializa variáveis de instância
        this.cor = cor;
        this.tipo = tipo;
        this.valor_numerico = valor_numerico;
    }
}