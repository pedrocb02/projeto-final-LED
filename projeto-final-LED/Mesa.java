import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe que gerencia a partida interativa do jogo Uno pelo terminal.
 * Controla os turnos, regras de jogadas, distribuição de cartas e interações.
 */
public class Mesa {

    /** Estrutura que mantém os jogadores da partida. */
    private List<Jogador> listaJogadores = new LinkedList<>();
    
    /** Índice do jogador atual na lista de jogadores. */
    private int indiceAtual = 0;

    /** Baralho principal de onde as cartas são compradas. */
    private List<Carta> baralho;
    
    /** Pilha de cartas descartadas (o topo dita as regras da rodada). */
    private List<Carta> mesaDescarte;
    
    /** Flag que indica a direção atual do jogo (verdadeiro para horário). */
    private boolean sentidoHorario;
    
    /** Objeto Scanner para capturar entradas de texto do terminal. */
    private Scanner entrada;

    /**
     * Construtor da classe Mesa.
     * Inicializa os baralhos, define o sentido inicial e embaralha as cartas.
     */
    public Mesa() {
        this.baralho      = new LinkedList<>();
        this.mesaDescarte = new LinkedList<>();
        this.sentidoHorario = true;
        this.entrada = new Scanner(System.in);

        inicializarBaralho();
        Collections.shuffle(baralho);
    }

    /**
     * Inicializa o baralho com as 108 cartas tradicionais do Uno.
     */
    private void inicializarBaralho() {
        for (Carta.Cor cor : Carta.Cor.values()) {
            if (cor == Carta.Cor.PRETO) {
                for (int i = 0; i < 4; i++) {
                    baralho.add(new Carta(cor, Carta.Tipo.MUDA_COR,    -1));
                    baralho.add(new Carta(cor, Carta.Tipo.MAIS_QUATRO, -1));
                }
            } else {
                for (int i = 0; i <= 9; i++) {
                    baralho.add(new Carta(cor, Carta.Tipo.NUMERO, i));
                }
                baralho.add(new Carta(cor, Carta.Tipo.INVERTER,  -1));
                baralho.add(new Carta(cor, Carta.Tipo.BLOQUEAR,  -1));
                baralho.add(new Carta(cor, Carta.Tipo.MAIS_DOIS, -1));
            }
        }
    }

    /**
     * Configura a quantidade de jogadores e seus respectivos nomes interagindo via terminal.
     */
    public void configurarJogadores() {
        System.out.print("Quantas pessoas vão jogar (mínimo 2)? ");
        int qtd = lerInteiro();

        while (qtd < 2) {
            System.out.print("Quantidade inválida! Digite pelo menos 2: ");
            qtd = lerInteiro();
        }

        for (int i = 1; i <= qtd; i++) {
            System.out.print("Digite o nome do Jogador " + i + ": ");
            String nome = entrada.nextLine();
            listaJogadores.add(new Jogador(nome));
        }
    }

    /**
     * Prepara a partida distribuindo cartas para os jogadores e definindo a carta inicial da mesa.
     */
    public void prepararPartida() {
        for (Jogador j : listaJogadores) {
            for (int i = 0; i < 7; i++) {
                j.adicionarCarta(baralho.remove(0));
            }
        }

        Carta primeiraCarta = baralho.remove(0);
        while (primeiraCarta.cor == Carta.Cor.PRETO) {
            baralho.add(primeiraCarta);
            primeiraCarta = baralho.remove(0);
        }
        mesaDescarte.add(primeiraCarta);
    }

    /**
     * Retorna o jogador que tem o direito de jogar neste turno.
     * * @return Objeto Jogador correspondente ao turno atual.
     */
    private Jogador jogadorAtual() {
        return listaJogadores.get(indiceAtual);
    }

    /**
     * Avança o turno atual para o próximo jogador, respeitando o sentido da roda (horário/anti-horário).
     */
    private void avancarTurno() {
        int n = listaJogadores.size();
        if (sentidoHorario) {
            indiceAtual = (indiceAtual + 1) % n;
        } else {
            indiceAtual = (indiceAtual - 1 + n) % n;
        }
    }

    /**
     * Verifica se uma jogada é válida comparando a carta escolhida com a carta no topo da mesa.
     * * @param cartaJogada A carta que o jogador deseja jogar.
     * @param cartaTopo A carta que se encontra atualmente no topo do descarte.
     * @return true se a jogada respeita as regras, false caso contrário.
     */
    private boolean jogadaEValida(Carta cartaJogada, Carta cartaTopo) {
        if (cartaJogada.cor == Carta.Cor.PRETO) return true;
        if (cartaJogada.cor == cartaTopo.cor) return true;
        if (cartaJogada.tipo == Carta.Tipo.NUMERO
                && cartaTopo.tipo == Carta.Tipo.NUMERO
                && cartaJogada.valor_numerico == cartaTopo.valor_numerico) return true;
        if (cartaJogada.tipo != Carta.Tipo.NUMERO
                && cartaJogada.tipo == cartaTopo.tipo) return true;

        return false;
    }

    /**
     * Solicita ao jogador via terminal que escolha uma nova cor ao jogar um coringa.
     * * @return A cor escolhida pelo jogador.
     */
    private Carta.Cor escolherCor() {
        System.out.println("Escolha a nova cor:");
        System.out.println("  1 - VERMELHO");
        System.out.println("  2 - AZUL");
        System.out.println("  3 - VERDE");
        System.out.println("  4 - AMARELO");

        while (true) {
            System.out.print("Opção: ");
            int op = lerInteiro();
            switch (op) {
                case 1: return Carta.Cor.VERMELHO;
                case 2: return Carta.Cor.AZUL;
                case 3: return Carta.Cor.VERDE;
                case 4: return Carta.Cor.AMARELO;
                default: System.out.println("Opção inválida! Digite 1, 2, 3 ou 4.");
            }
        }
    }

    /**
     * Processa os efeitos das cartas especiais (Inverter, Bloquear, +2, +4, Muda Cor)
     * e cuida do avanço de turnos subsequentes de acordo com as regras de cada carta.
     * * @param carta A carta recém-jogada que ativará o efeito na mesa.
     */
    private void processarEfeitoCarta(Carta carta) {
        Carta topoAtual = mesaDescarte.get(mesaDescarte.size() - 1);

        switch (carta.tipo) {
            case INVERTER:
                sentidoHorario = !sentidoHorario;
                System.out.println("O sentido do jogo foi invertido!");
                avancarTurno();
                break;
            case BLOQUEAR:
                avancarTurno();
                System.out.println("PAAA!!! Acesso negado " + jogadorAtual().getNome() + " foi bloqueado e perde a vez!");
                avancarTurno();
                break;
            case MAIS_DOIS:
                avancarTurno();
                Jogador vitima2 = jogadorAtual();
                System.out.println(vitima2.getNome() + " recebeu +2 cartas e perdeu a vez!");
                comprarCartas(vitima2, 2);
                avancarTurno();
                break;
            case MUDA_COR:
                Carta.Cor novaCor = escolherCor();
                topoAtual.cor = novaCor;
                System.out.println("Nova cor escolhida: " + novaCor);
                avancarTurno();
                break;
            case MAIS_QUATRO:
                Carta.Cor novaCorM4 = escolherCor();
                topoAtual.cor = novaCorM4;
                System.out.println("Nova cor escolhida: " + novaCorM4);
                avancarTurno();
                Jogador vitima4 = jogadorAtual();
                System.out.println(vitima4.getNome() + " tomou +4 cartas e perdeu a vez!");
                comprarCartas(vitima4, 4);
                avancarTurno();
                break;
            default:
                avancarTurno();
                break;
        }
    }

    /**
     * Executa a compra de um número específico de cartas do baralho para um jogador.
     * Recicla a mesa de descarte caso o baralho esvazie durante a compra.
     * * @param jogador O jogador que deve receber as cartas.
     * @param quantidade A quantidade de cartas a serem compradas.
     */
    private void comprarCartas(Jogador jogador, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            if (baralho.isEmpty()) {
                reciclarDescarte();
            }
            if (!baralho.isEmpty()) {
                jogador.adicionarCarta(baralho.remove(0));
            } else {
                System.out.println("Baralho esgotado! Não há cartas suficientes.");
                break;
            }
        }
    }

    /**
     * Pega todas as cartas descartadas (exceto o topo) e as mistura para formar um novo baralho.
     */
    private void reciclarDescarte() {
        if (mesaDescarte.size() <= 1) return;
        Carta topo = mesaDescarte.remove(mesaDescarte.size() - 1);
        baralho.addAll(mesaDescarte);
        mesaDescarte.clear();
        mesaDescarte.add(topo);
        Collections.shuffle(baralho);
        System.out.println("Descarte reciclado no baralho!");
    }

    /**
     * Utilitário para leitura segura de números inteiros via terminal.
     * Previne exceptions caso o utilizador digite letras onde se espera números.
     * * @return O número inteiro digitado.
     */
    private int lerInteiro() {
        while (!entrada.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            entrada.nextLine();
        }
        int v = entrada.nextInt();
        entrada.nextLine();
        return v;
    }

    /**
     * Loop principal de execução do jogo interativo.
     * Gerencia rodadas, validação de mãos e interação do usuário.
     */
    public void jogar() {
        configurarJogadores();
        prepararPartida();

        System.out.println("\n=========================================");
        System.out.println("      ★  O JOGO DO UNO COMEÇOU  ★");
        System.out.println("=========================================");

        while (true) {
            Jogador jogadorDaVez = jogadorAtual();
            Carta   topoMesa     = mesaDescarte.get(mesaDescarte.size() - 1);

            System.out.println("\n==================================================");
            System.out.println("Topo da Mesa : " + topoMesa);
            System.out.println("Vez de       : " + jogadorDaVez.getNome());
            System.out.println("--------------------------------------------------");
            System.out.println("Suas cartas:");
            jogadorDaVez.imprimirMao();
            System.out.println("--------------------------------------------------");

            boolean temOpcoes = false;
            for (Carta c : jogadorDaVez.getMao()) {
                if (jogadaEValida(c, topoMesa)) {
                    temOpcoes = true;
                    break;
                }
            }

            if (!temOpcoes) {
                System.out.println("Você não tem cartas válidas! Pressione ENTER para comprar uma carta.");
                entrada.nextLine();

                if (baralho.isEmpty()) reciclarDescarte();

                if (!baralho.isEmpty()) {
                    Carta comprada = baralho.remove(0);
                    jogadorDaVez.adicionarCarta(comprada);
                    System.out.println("Você comprou: " + comprada);

                    if (jogadaEValida(comprada, topoMesa)) {
                        System.out.print("Essa carta é válida! Deseja jogá-la agora? (S/N): ");
                        String resp = entrada.nextLine().trim().toUpperCase();
                        if (resp.equals("S")) {
                            jogadorDaVez.removerCarta(comprada);
                            mesaDescarte.add(comprada);
                            System.out.println(jogadorDaVez.getNome() + " Jogou " + comprada);

                            if (verificarVitoria(jogadorDaVez)) return;

                            processarEfeitoCarta(comprada);
                            continue;
                        }
                    } else {
                        System.out.println("A carta comprada não serve. Passando a vez...");
                    }
                } else {
                    System.out.println("O baralho acabou! Passando a vez...");
                }
                avancarTurno();

            } else {
                Carta cartaEscolhida = null;

                while (cartaEscolhida == null) {
                    System.out.print("Escolha o número da carta que quer jogar: ");
                    int escolha = lerInteiro();

                    if (escolha >= 0 && escolha < jogadorDaVez.getQuantidadeCartas()) {
                        Carta provisoria = jogadorDaVez.getMao().get(escolha);
                        if (jogadaEValida(provisoria, topoMesa)) {
                            cartaEscolhida = provisoria;
                        } else {
                            System.out.println("Essa carta não pode ser jogada! Escolha outra.");
                        }
                    } else {
                        System.out.println("Número inválido! Escolha entre 0 e "
                                + (jogadorDaVez.getQuantidadeCartas() - 1) + ".");
                    }
                }

                jogadorDaVez.removerCarta(cartaEscolhida);
                mesaDescarte.add(cartaEscolhida);
                System.out.println("\n" + jogadorDaVez.getNome() + " JOGOU " + cartaEscolhida + "!");

                if (verificarVitoria(jogadorDaVez)) return;

                processarEfeitoCarta(cartaEscolhida);
            }
        }
    }

    /**
     * Verifica se o jogador especificado atingiu as condições de vitória (0 cartas) 
     * ou alerta de "UNO!" (1 carta).
     * * @param jogador O jogador a ser avaliado.
     * @return true se o jogador venceu, false caso contrário.
     */
    private boolean verificarVitoria(Jogador jogador) {
        if (jogador.getQuantidadeCartas() == 0) {
            System.out.println("VITÓRIA! " + jogador.getNome() + " venceu o jogo de Uno!");
            return true;
        }
        if (jogador.getQuantidadeCartas() == 1) {
            System.out.println(jogador.getNome() + " grita: UNO!!!");
        }
        return false;
    }

    /**
     * Método de entrada do programa.
     * * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        Mesa mesa = new Mesa();
        mesa.jogar();
    }
}