import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe que gerencia a partida interativa do jogo Uno pelo terminal.
 */
public class Mesa {

    // =====================================================================
    //  ESTRUTURA DE DADOS SIMULADA (substitui ListaDuplamenteLigadaCircular)
    //  Mantém a semântica de lista circular com sentido configurável.
    // =====================================================================
    private List<Jogador> listaJogadores = new LinkedList<>();
    private int indiceAtual = 0;

    private List<Carta> baralho;
    private List<Carta> mesaDescarte;
    private boolean sentidoHorario;
    private Scanner entrada;

    public Mesa() {
        this.baralho      = new LinkedList<>();
        this.mesaDescarte = new LinkedList<>();
        this.sentidoHorario = true;
        this.entrada = new Scanner(System.in);

        inicializarBaralho();
        Collections.shuffle(baralho);
    }

    // =====================================================================
    //  INICIALIZAÇÃO
    // =====================================================================
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

    public void prepararPartida() {
        // Distribui 7 cartas para cada jogador
        for (Jogador j : listaJogadores) {
            for (int i = 0; i < 7; i++) {
                j.adicionarCarta(baralho.remove(0));
            }
        }

        // Abre a primeira carta válida (não pode ser coringa)
        Carta primeiraCarta = baralho.remove(0);
        while (primeiraCarta.cor == Carta.Cor.PRETO) {
            baralho.add(primeiraCarta);
            primeiraCarta = baralho.remove(0);
        }
        mesaDescarte.add(primeiraCarta);
    }

    // =====================================================================
    //  NAVEGAÇÃO CIRCULAR
    // =====================================================================
    private Jogador jogadorAtual() {
        return listaJogadores.get(indiceAtual);
    }

    private void avancarTurno() {
        int n = listaJogadores.size();
        if (sentidoHorario) {
            indiceAtual = (indiceAtual + 1) % n;
        } else {
            indiceAtual = (indiceAtual - 1 + n) % n;
        }
    }

    // =====================================================================
    //  REGRAS — VALIDAÇÃO DE JOGADA
    //  BUG ORIGINAL: só validava número igual OU carta preta.
    //  CORREÇÃO: valida mesma cor, mesmo número, mesmo tipo de ação, ou coringa.
    // =====================================================================
    private boolean jogadaEValida(Carta cartaJogada, Carta cartaTopo) {
        // Coringa sempre pode ser jogado
        if (cartaJogada.cor == Carta.Cor.PRETO) return true;

        // Mesma cor que o topo
        if (cartaJogada.cor == cartaTopo.cor) return true;

        // Mesma cor da cor escolhida (quando o topo é um coringa)
        // — cartaTopo.cor já foi atualizada pelo escolherCor(), então a linha acima cobre isso

        // Mesmo número
        if (cartaJogada.tipo == Carta.Tipo.NUMERO
                && cartaTopo.tipo == Carta.Tipo.NUMERO
                && cartaJogada.valor_numerico == cartaTopo.valor_numerico) return true;

        // Mesmo tipo de ação (ex.: Bloquear em cima de Bloquear de outra cor)
        if (cartaJogada.tipo != Carta.Tipo.NUMERO
                && cartaJogada.tipo == cartaTopo.tipo) return true;

        return false;
    }

    // =====================================================================
    //  ESCOLHA DE COR PARA CORINGAS
    //  BUG ORIGINAL: coringas nunca pediam cor — topo ficava PRETO para sempre.
    // =====================================================================
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

    // =====================================================================
    //  EFEITOS DAS CARTAS ESPECIAIS
    //  BUG ORIGINAL:
    //    • MUDA_COR caia no else e apenas avançava o turno, sem trocar a cor.
    //    • MAIS_QUATRO não pedia cor ao jogador.
    //    • Quando chamado após compra+jogo imediato, o turno era avançado
    //      duas vezes (uma no continue + uma aqui).
    //  CORREÇÃO: processarEfeitoCarta agora SEMPRE avança o turno internamente
    //  e retorna; o loop principal não avança mais de forma independente.
    // =====================================================================
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
                // Pede cor e atualiza a cor do topo (a própria carta no descarte)
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
                // Carta numérica normal
                avancarTurno();
                break;
        }
    }

    // =====================================================================
    //  UTILITÁRIO — compra cartas com reciclagem do descarte se necessário
    // =====================================================================
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

    private void reciclarDescarte() {
        if (mesaDescarte.size() <= 1) return;
        Carta topo = mesaDescarte.remove(mesaDescarte.size() - 1);
        baralho.addAll(mesaDescarte);
        mesaDescarte.clear();
        mesaDescarte.add(topo);
        Collections.shuffle(baralho);
        System.out.println("Descarte reciclado no baralho!");
    }

    // =====================================================================
    //  UTILITÁRIO — leitura segura de inteiro
    // =====================================================================
    private int lerInteiro() {
        while (!entrada.hasNextInt()) {
            System.out.print("Digite um número válido: ");
            entrada.nextLine();
        }
        int v = entrada.nextInt();
        entrada.nextLine();
        return v;
    }

    // =====================================================================
    //  LOOP PRINCIPAL
    // =====================================================================
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

            // Verifica se o jogador tem pelo menos uma carta jogável
            boolean temOpcoes = false;
            for (Carta c : jogadorDaVez.getMao()) {
                if (jogadaEValida(c, topoMesa)) {
                    temOpcoes = true;
                    break;
                }
            }

            if (!temOpcoes) {
                // --- SEM OPÇÕES: compra uma carta ---
                System.out.println("Você não tem cartas válidas! Pressione ENTER para comprar uma carta.");
                entrada.nextLine();

                if (baralho.isEmpty()) reciclarDescarte();

                if (!baralho.isEmpty()) {
                    Carta comprada = baralho.remove(0);
                    jogadorDaVez.adicionarCarta(comprada);
                    System.out.println("Você comprou: " + comprada);

                    // BUG ORIGINAL: carta comprada era removida do baralho mas
                    // nunca adicionada à mão antes de tentar jogá-la.
                    // CORREÇÃO: adicionamos primeiro, e se for jogável perguntamos
                    // se quer remover da mão e descartar.
                    if (jogadaEValida(comprada, topoMesa)) {
                        System.out.print("Essa carta é válida! Deseja jogá-la agora? (S/N): ");
                        String resp = entrada.nextLine().trim().toUpperCase();
                        if (resp.equals("S")) {
                            jogadorDaVez.removerCarta(comprada);
                            mesaDescarte.add(comprada);
                            System.out.println(jogadorDaVez.getNome() + " Jogou " + comprada);

                            if (verificarVitoria(jogadorDaVez)) return;

                            // BUG ORIGINAL: o continue pulava o avancarTurno do final,
                            // mas processarEfeitoCarta não avançava nesse caminho.
                            // CORREÇÃO: processarEfeitoCarta sempre avança o turno.
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
                // --- COM OPÇÕES: jogador escolhe a carta ---
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

    public static void main(String[] args) {
        Mesa mesa = new Mesa();
        mesa.jogar();
    }
}