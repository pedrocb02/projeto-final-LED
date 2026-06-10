import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Interface Gráfica para o jogo Uno, utilizando as classes Carta e Jogador.
 * Estende JFrame para fornecer uma janela de jogo visual e iterativa utilizando a API Swing.
 */
public class UnoGUI extends JFrame {

    /** Lista de jogadores participantes da partida. */
    private List<Jogador> listaJogadores = new LinkedList<>();
    
    /** Índice que aponta para o jogador dono do turno atual. */
    private int indiceAtual = 0;
    
    /** Estrutura do baralho principal do jogo. */
    private List<Carta> baralho = new LinkedList<>();
    
    /** Pilha da mesa onde as cartas descartadas são armazenadas. */
    private List<Carta> mesaDescarte = new LinkedList<>();
    
    /** Define se a rotação de turnos segue no sentido horário. */
    private boolean sentidoHorario = true;

    // Componentes da Interface Gráfica
    /** Painel central que abriga o topo do descarte e o botão de compra de cartas. */
    private JPanel painelMesa;
    
    /** Painel inferior com scroll onde a mão de cartas do jogador atual é exibida. */
    private JPanel painelMao;
    
    /** Label descritiva do status da partida (Quem joga, direção, quantidade de cartas). */
    private JLabel lblStatus;
    
    /** Label com o título sobre a carta do topo. */
    private JLabel lblTopo;
    
    /** Botão visual que representa o monte de compra do baralho. */
    private JButton btnComprar;

    /**
     * Construtor da interface gráfica do UNO.
     * Configura a janela, seu tamanho e dispara o setup inicial da partida.
     */
    public UnoGUI() {
        super("Jogo Uno Gráfico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        configurarPartidaGUI();
    }

    /**
     * Utiliza caixas de diálogo do Swing para capturar o número de jogadores e seus nomes.
     */
    private void configurarPartidaGUI() {
        String qtdStr = JOptionPane.showInputDialog(this, "Quantas pessoas vão jogar (mínimo 2)?", "Configuração", JOptionPane.QUESTION_MESSAGE);
        if (qtdStr == null) System.exit(0);

        try {
            int qtd = Integer.parseInt(qtdStr);
            if (qtd < 2) throw new NumberFormatException();

            for (int i = 1; i <= qtd; i++) {
                String nome = JOptionPane.showInputDialog(this, "Nome do Jogador " + i + ":");
                if (nome == null || nome.trim().isEmpty()) nome = "Jogador " + i;
                listaJogadores.add(new Jogador(nome));
            }

            iniciarJogo();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida. Tente novamente.");
            configurarPartidaGUI();
        }
    }

    /**
     * Engatilha o fluxo inicial do jogo: inicializa baralho, distribui cartas e desenha a UI.
     */
    private void iniciarJogo() {
        inicializarBaralho();
        Collections.shuffle(baralho);
        prepararPartida();
        construirInterfacePrincipal();
        atualizarEcra();
    }

    /**
     * Instancia e posiciona os painéis principais do Swing e seus componentes básicos.
     */
    private void construirInterfacePrincipal() {
        JPanel painelStatus = new JPanel(new GridLayout(2, 1));
        painelStatus.setBackground(new Color(40, 40, 40));
        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 16));
        lblTopo = new JLabel("", SwingConstants.CENTER);
        lblTopo.setForeground(Color.YELLOW);
        lblTopo.setFont(new Font("Arial", Font.BOLD, 24));
        painelStatus.add(lblStatus);
        painelStatus.add(lblTopo);
        add(painelStatus, BorderLayout.NORTH);

        painelMesa = new JPanel(new GridBagLayout()); 
        painelMesa.setBackground(new Color(34, 139, 34)); 
        
        btnComprar = new JButton("Baralho (Comprar)");
        btnComprar.setPreferredSize(new Dimension(100, 150));
        btnComprar.setFont(new Font("Arial", Font.BOLD, 12));
        btnComprar.addActionListener(e -> comprarCartaGUI());
        
        add(painelMesa, BorderLayout.CENTER);

        painelMao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelMao.setBackground(new Color(60, 60, 60));
        JScrollPane scrollMao = new JScrollPane(painelMao);
        scrollMao.setPreferredSize(new Dimension(800, 170));
        add(scrollMao, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    /**
     * Inicializa o baralho com as 108 cartas tradicionais utilizando a classe Carta.
     */
    private void inicializarBaralho() {
        for (Carta.Cor cor : Carta.Cor.values()) {
            if (cor == Carta.Cor.PRETO) {
                for (int i = 0; i < 4; i++) {
                    baralho.add(new Carta(cor, Carta.Tipo.MUDA_COR, -1));
                    baralho.add(new Carta(cor, Carta.Tipo.MAIS_QUATRO, -1));
                }
            } else {
                baralho.add(new Carta(cor, Carta.Tipo.NUMERO, 0));

                for (int i = 1; i <= 9; i++) {
                    baralho.add(new Carta(cor, Carta.Tipo.NUMERO, i));
                    baralho.add(new Carta(cor, Carta.Tipo.NUMERO, i));
                }

                for (int i = 0; i < 2; i++) {
                    baralho.add(new Carta(cor, Carta.Tipo.INVERTER, -1));
                    baralho.add(new Carta(cor, Carta.Tipo.BLOQUEAR, -1));
                    baralho.add(new Carta(cor, Carta.Tipo.MAIS_DOIS, -1));
                }
            }
        }
    }

    /**
     * Distribui 7 cartas iniciais para cada jogador e puxa a carta inicial para o descarte.
     */
    private void prepararPartida() {
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
     * Obtém a instância do jogador cujo turno está ativo no momento.
     * * @return O Jogador atual.
     */
    private Jogador jogadorAtual() {
        return listaJogadores.get(indiceAtual);
    }

    /**
     * Procede para o próximo turno do jogo, baseando-se na variável de sentido horário.
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
     * Valida se uma carta específica pode ser descartada de acordo com as regras.
     * * @param cartaJogada A carta selecionada pelo jogador.
     * @param cartaTopo A carta no topo da mesa.
     * @return true se a jogada é legal e validada, false caso não seja permitida.
     */
    private boolean jogadaEValida(Carta cartaJogada, Carta cartaTopo) {
        if (cartaJogada.cor == Carta.Cor.PRETO) return true;
        if (cartaJogada.cor == cartaTopo.cor) return true;
        if (cartaJogada.tipo == Carta.Tipo.NUMERO && cartaTopo.tipo == Carta.Tipo.NUMERO && cartaJogada.valor_numerico == cartaTopo.valor_numerico) return true;
        if (cartaJogada.tipo != Carta.Tipo.NUMERO && cartaJogada.tipo == cartaTopo.tipo) return true;
        return false;
    }

    /**
     * Recarrega e repinta a interface gráfica inteira (painel da mesa e da mão) baseado no estado atual da partida.
     */
    private void atualizarEcra() {
        Jogador atual = jogadorAtual();
        Carta topoMesa = mesaDescarte.get(mesaDescarte.size() - 1);

        String direcao = sentidoHorario ? "Horário" : "Anti-Horário";
        lblStatus.setText("Vez de: " + atual.getNome() + " | Sentido: " + direcao + " | Baralho: " + baralho.size() + " cartas");
        lblTopo.setText("Mesa Atual");

        painelMesa.removeAll();
        painelMesa.add(btnComprar); 
        painelMesa.add(Box.createHorizontalStrut(30)); 
        
        JButton btnTopo = criarBotaoCarta(topoMesa);
        btnTopo.setEnabled(false); 
        painelMesa.add(btnTopo);
        
        painelMesa.revalidate();
        painelMesa.repaint();

        painelMao.removeAll();
        for (Carta carta : atual.getMao()) {
            JButton btnCarta = criarBotaoCarta(carta);
            boolean valida = jogadaEValida(carta, topoMesa);
            
            if (valida) {
                btnCarta.addActionListener(e -> jogarCarta(carta));
            } else {
                btnCarta.setEnabled(false); 
            }
            painelMao.add(btnCarta);
        }

        painelMao.revalidate();
        painelMao.repaint();
    }

    /**
     * Constrói graficamente um componente JButton que ilustra e representa visualmente uma carta do baralho.
     * * @param carta Objeto lógico da carta que será transformada num botão de UI.
     * @return Componente JButton esteticamente formatado com renderização 2D.
     */
    private JButton criarBotaoCarta(Carta carta) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (carta.tipo != Carta.Tipo.NUMERO) {
                    String caminhoImagem = "imagens/" + carta.cor.toString() + "_" + carta.tipo.toString() + ".png";
                    ImageIcon imagemEspecial = new ImageIcon(caminhoImagem);
                    
                    if (imagemEspecial.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        g2.drawImage(imagemEspecial.getImage(), 0, 0, getWidth(), getHeight(), this);
                    } else {
                        desenharCartaPorCodigo(g2, carta, getWidth(), getHeight()); 
                    }
                } else {
                    String caminhoFundo = "imagens/fundo_" + carta.cor.toString().toLowerCase() + ".png";
                    ImageIcon iconeFundo = new ImageIcon(caminhoFundo);
                    
                    if (iconeFundo.getImageLoadStatus() == MediaTracker.COMPLETE) {
                        g2.drawImage(iconeFundo.getImage(), 0, 0, getWidth(), getHeight(), this);
                        
                        g2.setColor(carta.cor == Carta.Cor.AMARELO ? Color.BLACK : Color.WHITE);
                        String simbolo = String.valueOf(carta.valor_numerico);
                        
                        g2.setFont(new Font("Arial", Font.BOLD, 45));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(simbolo, (getWidth() - fm.stringWidth(simbolo)) / 2, (getHeight() / 2) + (fm.getAscent() / 3));
                        
                        g2.setFont(new Font("Arial", Font.BOLD, 14));
                        g2.drawString(simbolo, 8, 20);
                        g2.translate(getWidth() - 8, getHeight() - 8);
                        g2.rotate(Math.PI);
                        g2.drawString(simbolo, 0, 4);
                        g2.rotate(-Math.PI);
                        g2.translate(-(getWidth() - 8), -(getHeight() - 8));
                    } else {
                        desenharCartaPorCodigo(g2, carta, getWidth(), getHeight()); 
                    }
                }

                if (!isEnabled()) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                }
            }
        };

        btn.setPreferredSize(new Dimension(100, 150));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    /**
     * Fallback gráfico que desenha nativamente com a API Graphics2D caso falte alguma imagem .png no repositório.
     * * @param g2 Contexto gráfico 2D do componente.
     * @param carta Carta a ser desenhada.
     * @param largura Largura do painel/botão.
     * @param altura Altura do painel/botão.
     */
    private void desenharCartaPorCodigo(Graphics2D g2, Carta carta, int largura, int altura) {
        g2.setColor(obterCorJava(carta.cor));
        g2.fillRoundRect(0, 0, largura, altura, 16, 16);
        
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(2, 2, largura - 4, altura - 4, 16, 16);

        if (carta.cor != Carta.Cor.PRETO) {
            g2.rotate(-Math.PI / 6, largura / 2.0, altura / 2.0);
            g2.fillOval(10, 25, largura - 20, altura - 50);
            g2.rotate(Math.PI / 6, largura / 2.0, altura / 2.0);
        }

        String simbolo = obterSimboloDaCarta(carta);
        
        g2.setColor(Color.BLACK);
        if(carta.cor == Carta.Cor.PRETO) g2.setColor(Color.WHITE); 
        g2.setFont(new Font("Arial", Font.BOLD, 45));
        FontMetrics fm = g2.getFontMetrics();
        int xCentro = (largura - fm.stringWidth(simbolo)) / 2;
        int yCentro = (altura / 2) + (fm.getAscent() / 3);
        g2.drawString(simbolo, xCentro, yCentro);

        g2.setColor(Color.WHITE);
        if (carta.cor == Carta.Cor.AMARELO) g2.setColor(Color.BLACK); 
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(simbolo, 8, 20);

        g2.translate(largura - 8, altura - 8);
        g2.rotate(Math.PI);
        g2.drawString(simbolo, 0, 4);
        g2.rotate(-Math.PI);
        g2.translate(-(largura - 8), -(altura - 8));
    }

    /**
     * Retorna a representação textual apropriada/ícone em modo caracter para renderização visual.
     * * @param carta Objeto carta de origem.
     * @return String contendo o texto (números) ou símbolo da respectiva ação.
     */
    private String obterSimboloDaCarta(Carta carta) {
        if (carta.tipo == Carta.Tipo.NUMERO) {
            return String.valueOf(carta.valor_numerico);
        }
        switch (carta.tipo) {
            case MAIS_DOIS: return "+2";
            case MAIS_QUATRO: return "+4";
            case INVERTER: return "↺";
            case BLOQUEAR: return "Ø";
            case MUDA_COR: return "cor";
            default: return "";
        }
    }

    /**
     * Mapeia as propriedades da Enum Carta.Cor para instâncias de java.awt.Color
     * * @param cor Enumeração da cor desejada.
     * @return Cor computacional nativa da API Swing.
     */
    private Color obterCorJava(Carta.Cor cor) {
        switch (cor) {
            case VERMELHO: return new Color(220, 20, 60);
            case AZUL: return new Color(30, 144, 255);
            case VERDE: return new Color(34, 139, 34);
            case AMARELO: return new Color(255, 215, 0);
            case PRETO: return Color.DARK_GRAY;
            default: return Color.WHITE;
        }
    }

    /**
     * Rotina executada sempre que um usuário clica numa carta válida em sua mão.
     * Transfere a carta da mão para o descarte, valida vitória e roda os efeitos especiais.
     * * @param cartaEscolhida Carta selecionada via interface.
     */
    private void jogarCarta(Carta cartaEscolhida) {
        Jogador atual = jogadorAtual();
        atual.removerCarta(cartaEscolhida);
        mesaDescarte.add(cartaEscolhida);

        if (verificarVitoria(atual)) return;

        processarEfeitoCartaGUI(cartaEscolhida);
    }

    /**
     * Desdobra as consequências de ações na GUI, avançando turnos, exibindo prompts
     * e forçando saques de carta.
     * * @param carta Carta jogada que instigará as ações subsequentes.
     */
    private void processarEfeitoCartaGUI(Carta carta) {
        Carta topoAtual = mesaDescarte.get(mesaDescarte.size() - 1);

        switch (carta.tipo) {
            case INVERTER:
                sentidoHorario = !sentidoHorario;
                JOptionPane.showMessageDialog(this, "O sentido do jogo foi invertido!");
                avancarTurno();
                break;
            case BLOQUEAR:
                avancarTurno();
                JOptionPane.showMessageDialog(this, "PAAA!!! Acesso Negado!!! " + jogadorAtual().getNome() + " foi bloqueado e perde a vez!");
                avancarTurno();
                break;
            case MAIS_DOIS:
                avancarTurno();
                Jogador vitima2 = jogadorAtual();
                comprarCartasInterno(vitima2, 2);
                JOptionPane.showMessageDialog(this, vitima2.getNome() + " recebeu +2 cartas e perdeu a vez!");
                avancarTurno();
                break;
            case MUDA_COR:
                topoAtual.cor = escolherCorGUI();
                avancarTurno();
                break;
            case MAIS_QUATRO:
                topoAtual.cor = escolherCorGUI();
                avancarTurno();
                Jogador vitima4 = jogadorAtual();
                comprarCartasInterno(vitima4, 4);
                JOptionPane.showMessageDialog(this, vitima4.getNome() + " tomou +4 cartas e perdeu a vez!");
                avancarTurno();
                break;
            default:
                avancarTurno();
                break;
        }
        atualizarEcra();
    }

    /**
     * Dispara um modal (JOptionPane) que obriga o jogador atual a escolher a nova cor do descarte via menu.
     * * @return Enum Cor que substituirá o preto da carta do descarte.
     */
    private Carta.Cor escolherCorGUI() {
        Object[] opcoes = {"VERMELHO", "AZUL", "VERDE", "AMARELO"};
        int resposta = JOptionPane.showOptionDialog(this,
                "Escolha a nova cor:",
                "Mudar Cor",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        switch (resposta) {
            case 0: return Carta.Cor.VERMELHO;
            case 1: return Carta.Cor.AZUL;
            case 2: return Carta.Cor.VERDE;
            case 3: return Carta.Cor.AMARELO;
            default: return Carta.Cor.VERMELHO; 
        }
    }

    /**
     * Evento atrelado ao clique no botão da pilha de saque.
     * O jogador adquire uma nova carta e recebe a opção de jogar imediatamente caso seja legal.
     */
    private void comprarCartaGUI() {
        Jogador atual = jogadorAtual();
        
        if (baralho.isEmpty()) reciclarDescarte();
        if (baralho.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O baralho acabou!");
            avancarTurno();
            atualizarEcra();
            return;
        }

        Carta comprada = baralho.remove(0);
        atual.adicionarCarta(comprada);
        Carta topoMesa = mesaDescarte.get(mesaDescarte.size() - 1);

        if (jogadaEValida(comprada, topoMesa)) {
            int resposta = JOptionPane.showConfirmDialog(this, 
                "Comprou a carta: " + comprada.toString() + ".\nEsta carta é válida! Deseja jogá-la agora?", 
                "Carta Comprada", JOptionPane.YES_NO_OPTION);
            
            if (resposta == JOptionPane.YES_OPTION) {
                atual.removerCarta(comprada);
                mesaDescarte.add(comprada);
                if (verificarVitoria(atual)) return;
                processarEfeitoCartaGUI(comprada);
                return; 
            }
        } else {
            JOptionPane.showMessageDialog(this, "Comprou a carta: " + comprada.toString() + ".\nEla não serve para jogar. Passando a vez...");
        }

        avancarTurno();
        atualizarEcra();
    }

    /**
     * Função interna e silenciosa utilizada pelos efeitos +2 e +4 para transferir as penalidades.
     * * @param jogador O Jogador alvo da punição.
     * @param quantidade O número respectivo da punição.
     */
    private void comprarCartasInterno(Jogador jogador, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            if (baralho.isEmpty()) reciclarDescarte();
            if (!baralho.isEmpty()) {
                jogador.adicionarCarta(baralho.remove(0));
            }
        }
    }

    /**
     * Renova as pilhas pegando tudo exceto o topo do descarte e jogando no baralho principal.
     */
    private void reciclarDescarte() {
        if (mesaDescarte.size() <= 1) return;
        Carta topo = mesaDescarte.remove(mesaDescarte.size() - 1);
        baralho.addAll(mesaDescarte);
        mesaDescarte.clear();
        mesaDescarte.add(topo);
        Collections.shuffle(baralho);
        JOptionPane.showMessageDialog(this, "Baralho vazio! Descarte reciclado.");
    }

    /**
     * Avalia ao fim de cada jogada se algum jogador zerou a mão. 
     * Acusa "UNO!" visualmente quando um jogador tem apenas uma carta na GUI.
     * * @param jogador Jogador que executou a última jogada.
     * @return true se confirmada a vitória encerrando a JVM, false no modo contrário.
     */
    private boolean verificarVitoria(Jogador jogador) {
        if (jogador.getQuantidadeCartas() == 0) {
            JOptionPane.showMessageDialog(this, "VITÓRIA!\n" + jogador.getNome() + " venceu o jogo de Uno!");
            System.exit(0);
            return true;
        }
        if (jogador.getQuantidadeCartas() == 1) {
            JOptionPane.showMessageDialog(this, jogador.getNome() + " grita: UNO!!!");
        }
        return false;
    }

    /**
     * Entrada principal para instanciar a versão com interface gráfica do jogo.
     * * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            UnoGUI jogo = new UnoGUI();
            jogo.setVisible(true);
        });
    }
}