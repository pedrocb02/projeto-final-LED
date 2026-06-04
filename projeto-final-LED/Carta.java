
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
    
    //getters
    public Cor getCor(){
        return this.cor;
    }
    
    public Tipo getTipo(){
        return this.tipo;
    }
    
    public int getValor(){
        return this.valor_numerico;
    }
    
    public boolean cartaJogavel(Carta cartaDaMesa){
        boolean jogavel = false;
        //this = carta na mao do jogador
        //cartaDaMesa = topo da pilha
        if(cartaDaMesa.cor == this.cor || this.cor == Cor.PRETO || this.tipo == cartaDaMesa.tipo){
            jogavel = true;
        }
        return jogavel;
    }
}