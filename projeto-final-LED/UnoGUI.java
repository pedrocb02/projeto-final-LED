import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Interface grafica para o jogo Uno, utilizando as classes Carta e Jogador.
 */
public class UnoGUI extends JFrame {

    private static final Color COR_FUNDO = new Color(18, 24, 32);
    private static final Color COR_PAINEL = new Color(29, 38, 50);
    private static final Color COR_MESA = new Color(24, 118, 82);
    private static final Color COR_MESA_CLARA = new Color(39, 157, 109);
    private static final Color COR_TEXTO = new Color(245, 247, 250);
    private static final Color COR_TEXTO_SUAVE = new Color(190, 202, 214);
    private static final Color COR_DESTAQUE = new Color(255, 196, 64);
    private static final Dimension TAMANHO_CARTA = new Dimension(104, 156);

    /** Lista de jogadores participantes da partida. */
    private List<Jogador> listaJogadores = new LinkedList<>();

    /** Indice que aponta para o jogador dono do turno atual. */
    private int indiceAtual = 0;

    /** Estrutura do baralho principal do jogo. */
    private List<Carta> baralho = new LinkedList<>();

    /** Pilha da mesa onde as cartas descartadas sao armazenadas. */
    private List<Carta> mesaDescarte = new LinkedList<>();

    /** Define se a rotacao de turnos segue no sentido horario. */
    private boolean sentidoHorario = true;

    private JPanel painelMesa;
    private JPanel painelMao;
    private JLabel lblStatus;
    private JLabel lblTopo;
    private JLabel lblJogadores;
    private JButton btnComprar;

    public UnoGUI() {
        super("UNO - Mesa de Jogo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 660));
        setSize(980, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_FUNDO);

        configurarPartidaGUI();
    }

    private void configurarPartidaGUI() {
        String qtdStr = JOptionPane.showInputDialog(this, "Quantas pessoas vao jogar (minimo 2)?", "Configuracao", JOptionPane.QUESTION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Quantidade invalida. Tente novamente.");
            configurarPartidaGUI();
        }
    }

    private void iniciarJogo() {
        inicializarBaralho();
        Collections.shuffle(baralho);
        prepararPartida();
        construirInterfacePrincipal();
        atualizarEcra();
    }

    private void construirInterfacePrincipal() {
        JPanel painelStatus = new JPanel(new GridLayout(3, 1, 0, 4));
        painelStatus.setBackground(COR_PAINEL);
        painelStatus.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setForeground(COR_TEXTO);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 18));

        lblTopo = new JLabel("", SwingConstants.CENTER);
        lblTopo.setForeground(COR_DESTAQUE);
        lblTopo.setFont(new Font("SansSerif", Font.BOLD, 24));

        lblJogadores = new JLabel("", SwingConstants.CENTER);
        lblJogadores.setForeground(COR_TEXTO_SUAVE);
        lblJogadores.setFont(new Font("SansSerif", Font.PLAIN, 13));

        painelStatus.add(lblStatus);
        painelStatus.add(lblTopo);
        painelStatus.add(lblJogadores);
        add(painelStatus, BorderLayout.NORTH);

        painelMesa = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint fundo = new GradientPaint(0, 0, COR_MESA_CLARA, getWidth(), getHeight(), COR_MESA);
                g2.setPaint(fundo);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 26));
                g2.setStroke(new BasicStroke(3));
                int ovalLargura = Math.max(420, getWidth() - 180);
                int ovalAltura = Math.max(230, getHeight() - 90);
                g2.drawOval((getWidth() - ovalLargura) / 2, (getHeight() - ovalAltura) / 2, ovalLargura, ovalAltura);
                g2.dispose();
            }
        };
        painelMesa.setBorder(BorderFactory.createEmptyBorder(34, 34, 34, 34));

        btnComprar = new JButton("Comprar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                desenharVersoCarta(g2, getWidth(), getHeight());
                g2.dispose();
            }
        };
        btnComprar.setPreferredSize(TAMANHO_CARTA);
        btnComprar.setToolTipText("Comprar uma carta do baralho");
        btnComprar.setContentAreaFilled(false);
        btnComprar.setBorderPainted(false);
        btnComprar.setFocusPainted(false);
        btnComprar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComprar.addActionListener(e -> comprarCartaGUI());

        add(painelMesa, BorderLayout.CENTER);

        painelMao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelMao.setBackground(COR_FUNDO);
        painelMao.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JScrollPane scrollMao = new JScrollPane(painelMao);
        scrollMao.setPreferredSize(new Dimension(900, 196));
        scrollMao.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 28)));
        scrollMao.getViewport().setBackground(COR_FUNDO);
        scrollMao.getHorizontalScrollBar().setUnitIncrement(18);
        add(scrollMao, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

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

    private boolean jogadaEValida(Carta cartaJogada, Carta cartaTopo) {
        if (cartaJogada.cor == Carta.Cor.PRETO) return true;
        if (cartaJogada.cor == cartaTopo.cor) return true;
        if (cartaJogada.tipo == Carta.Tipo.NUMERO && cartaTopo.tipo == Carta.Tipo.NUMERO && cartaJogada.valor_numerico == cartaTopo.valor_numerico) return true;
        if (cartaJogada.tipo != Carta.Tipo.NUMERO && cartaJogada.tipo == cartaTopo.tipo) return true;
        return false;
    }

    private void atualizarEcra() {
        Jogador atual = jogadorAtual();
        Carta topoMesa = mesaDescarte.get(mesaDescarte.size() - 1);

        String direcao = sentidoHorario ? "Horario" : "Anti-horario";
        lblStatus.setText("Vez de " + atual.getNome() + "  |  Sentido: " + direcao + "  |  Baralho: " + baralho.size() + " cartas");
        lblTopo.setText("Carta da mesa: " + topoMesa.toString());
        lblJogadores.setText(resumoJogadores(atual));

        painelMesa.removeAll();
        painelMesa.add(criarAreaMesa("Baralho", btnComprar));
        painelMesa.add(Box.createHorizontalStrut(46));

        JButton btnTopo = criarBotaoCarta(topoMesa);
        btnTopo.setEnabled(false);
        painelMesa.add(criarAreaMesa("Descarte", btnTopo));

        painelMesa.revalidate();
        painelMesa.repaint();

        painelMao.removeAll();
        for (Carta carta : atual.getMao()) {
            JButton btnCarta = criarBotaoCarta(carta);
            boolean valida = jogadaEValida(carta, topoMesa);

            if (valida) {
                btnCarta.addActionListener(e -> jogarCarta(carta));
                btnCarta.setToolTipText("Jogar " + carta.toString());
            } else {
                btnCarta.setEnabled(false);
                btnCarta.setToolTipText("Esta carta ainda nao pode ser jogada");
            }
            painelMao.add(btnCarta);
        }

        painelMao.revalidate();
        painelMao.repaint();
    }

    private JButton criarBotaoCarta(Carta carta) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                desenharCartaPorCodigo(g2, carta, getWidth(), getHeight());

                if (!isEnabled()) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
                    g2.setColor(new Color(5, 8, 12, 130));
                    g2.fillRoundRect(2, 2, getWidth() - 8, getHeight() - 10, 22, 22);
                }
                g2.dispose();
            }
        };

        btn.setPreferredSize(TAMANHO_CARTA);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void desenharCartaPorCodigo(Graphics2D g2, Carta carta, int largura, int altura) {
        g2.setColor(new Color(0, 0, 0, 65));
        g2.fillRoundRect(5, 7, largura - 10, altura - 10, 22, 22);

        Shape cartaClip = new RoundRectangle2D.Double(2, 2, largura - 8, altura - 10, 22, 22);
        g2.setClip(cartaClip);
        GradientPaint fundo = new GradientPaint(0, 0, clarear(obterCorJava(carta.cor), 32), largura, altura, escurecer(obterCorJava(carta.cor), 34));
        g2.setPaint(fundo);
        g2.fill(cartaClip);

        g2.setColor(new Color(255, 255, 255, 36));
        g2.fillOval(-largura / 3, -altura / 4, largura, altura / 2);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(5, 5, largura - 14, altura - 16, 18, 18);

        if (carta.cor != Carta.Cor.PRETO) {
            g2.rotate(-Math.PI / 7, largura / 2.0, altura / 2.0);
            g2.setColor(new Color(255, 255, 255, 230));
            g2.fillOval(12, 33, largura - 28, altura - 66);
            g2.rotate(Math.PI / 7, largura / 2.0, altura / 2.0);
        } else {
            desenharCoresCoringa(g2, largura, altura);
        }

        String simbolo = obterSimboloDaCarta(carta);

        g2.setClip(null);
        g2.setColor(carta.tipo == Carta.Tipo.NUMERO ? Color.BLACK : Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, simbolo.length() > 2 ? 34 : 50));
        FontMetrics fm = g2.getFontMetrics();
        int xCentro = (largura - fm.stringWidth(simbolo)) / 2;
        int yCentro = (altura / 2) + (fm.getAscent() / 3);
        g2.drawString(simbolo, xCentro, yCentro);

        g2.setColor(carta.tipo == Carta.Tipo.NUMERO ? Color.BLACK : Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString(simbolo, 8, 20);

        g2.translate(largura - 8, altura - 8);
        g2.rotate(Math.PI);
        g2.drawString(simbolo, 0, 4);
        g2.rotate(-Math.PI);
        g2.translate(-(largura - 8), -(altura - 8));
    }

    private String obterSimboloDaCarta(Carta carta) {
        if (carta.tipo == Carta.Tipo.NUMERO) {
            return String.valueOf(carta.valor_numerico);
        }
        switch (carta.tipo) {
            case MAIS_DOIS: return "+2";
            case MAIS_QUATRO: return "+4";
            case INVERTER: return "REV";
            case BLOQUEAR: return "X";
            case MUDA_COR: return "COR";
            default: return "";
        }
    }

    private Color obterCorJava(Carta.Cor cor) {
        switch (cor) {
            case VERMELHO: return new Color(220, 20, 60);
            case AZUL: return new Color(30, 144, 255);
            case VERDE: return new Color(34, 139, 34);
            case AMARELO: return new Color(255, 215, 0);
            case PRETO: return new Color(34, 38, 44);
            default: return Color.WHITE;
        }
    }

    private JPanel criarAreaMesa(String titulo, JComponent componente) {
        JPanel area = new JPanel(new BorderLayout(0, 10));
        area.setOpaque(false);

        JLabel label = new JLabel(titulo, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));

        area.add(label, BorderLayout.NORTH);
        area.add(componente, BorderLayout.CENTER);
        return area;
    }

    private String resumoJogadores(Jogador atual) {
        StringBuilder resumo = new StringBuilder("Maos: ");
        for (int i = 0; i < listaJogadores.size(); i++) {
            Jogador jogador = listaJogadores.get(i);
            if (i > 0) resumo.append("   ");
            resumo.append(jogador == atual ? ">" : "");
            resumo.append(jogador.getNome()).append(" (").append(jogador.getQuantidadeCartas()).append(")");
        }
        return resumo.toString();
    }

    private Color clarear(Color cor, int quantidade) {
        return new Color(
                Math.min(255, cor.getRed() + quantidade),
                Math.min(255, cor.getGreen() + quantidade),
                Math.min(255, cor.getBlue() + quantidade));
    }

    private Color escurecer(Color cor, int quantidade) {
        return new Color(
                Math.max(0, cor.getRed() - quantidade),
                Math.max(0, cor.getGreen() - quantidade),
                Math.max(0, cor.getBlue() - quantidade));
    }

    private void desenharVersoCarta(Graphics2D g2, int largura, int altura) {
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(5, 7, largura - 10, altura - 10, 22, 22);
        GradientPaint fundo = new GradientPaint(0, 0, new Color(33, 43, 60), largura, altura, new Color(12, 17, 25));
        g2.setPaint(fundo);
        g2.fillRoundRect(2, 2, largura - 8, altura - 10, 22, 22);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(5, 5, largura - 14, altura - 16, 18, 18);

        g2.rotate(-Math.PI / 7, largura / 2.0, altura / 2.0);
        g2.setColor(new Color(226, 32, 45));
        g2.fillOval(16, 36, largura - 32, altura - 72);
        g2.rotate(Math.PI / 7, largura / 2.0, altura / 2.0);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        String texto = "UNO";
        g2.drawString(texto, (largura - fm.stringWidth(texto)) / 2, altura / 2 + fm.getAscent() / 3);
    }

    private void desenharCoresCoringa(Graphics2D g2, int largura, int altura) {
        int centroX = largura / 2;
        int centroY = altura / 2;
        int raio = 33;
        g2.setColor(obterCorJava(Carta.Cor.VERMELHO));
        g2.fillArc(centroX - raio, centroY - raio, raio * 2, raio * 2, 0, 90);
        g2.setColor(obterCorJava(Carta.Cor.AZUL));
        g2.fillArc(centroX - raio, centroY - raio, raio * 2, raio * 2, 90, 90);
        g2.setColor(obterCorJava(Carta.Cor.VERDE));
        g2.fillArc(centroX - raio, centroY - raio, raio * 2, raio * 2, 180, 90);
        g2.setColor(obterCorJava(Carta.Cor.AMARELO));
        g2.fillArc(centroX - raio, centroY - raio, raio * 2, raio * 2, 270, 90);
    }

    private void jogarCarta(Carta cartaEscolhida) {
        Jogador atual = jogadorAtual();
        atual.removerCarta(cartaEscolhida);
        mesaDescarte.add(cartaEscolhida);

        if (verificarVitoria(atual)) return;

        processarEfeitoCartaGUI(cartaEscolhida);
    }

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
                JOptionPane.showMessageDialog(this, jogadorAtual().getNome() + " foi bloqueado e perde a vez!");
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
                JOptionPane.showMessageDialog(this, vitima4.getNome() + " recebeu +4 cartas e perdeu a vez!");
                avancarTurno();
                break;
            default:
                avancarTurno();
                break;
        }
        atualizarEcra();
    }

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
                "Comprou a carta: " + comprada.toString() + ".\nEsta carta e valida! Deseja joga-la agora?",
                "Carta Comprada", JOptionPane.YES_NO_OPTION);

            if (resposta == JOptionPane.YES_OPTION) {
                atual.removerCarta(comprada);
                mesaDescarte.add(comprada);
                if (verificarVitoria(atual)) return;
                processarEfeitoCartaGUI(comprada);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Comprou a carta: " + comprada.toString() + ".\nEla nao serve para jogar. Passando a vez...");
        }

        avancarTurno();
        atualizarEcra();
    }

    private void comprarCartasInterno(Jogador jogador, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            if (baralho.isEmpty()) reciclarDescarte();
            if (!baralho.isEmpty()) {
                jogador.adicionarCarta(baralho.remove(0));
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
        JOptionPane.showMessageDialog(this, "Baralho vazio! Descarte reciclado.");
    }

    private boolean verificarVitoria(Jogador jogador) {
        if (jogador.getQuantidadeCartas() == 0) {
            JOptionPane.showMessageDialog(this, "VITORIA!\n" + jogador.getNome() + " venceu o jogo de Uno!");
            System.exit(0);
            return true;
        }
        if (jogador.getQuantidadeCartas() == 1) {
            JOptionPane.showMessageDialog(this, jogador.getNome() + " grita: UNO!!!");
        }
        return false;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}

        SwingUtilities.invokeLater(() -> {
            UnoGUI jogo = new UnoGUI();
            jogo.setVisible(true);
        });
    }
}
