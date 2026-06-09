import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Interface Gráfica para o jogo Uno, utilizando as classes Carta e Jogador.
 */
public class UnoGUI extends JFrame {

    // Lógica de jogo (adaptada da sua classe Mesa)
    private List<Jogador> listaJogadores = new LinkedList<>();
    private int indiceAtual = 0;
    private List<Carta> baralho = new LinkedList<>();
    private List<Carta> mesaDescarte = new LinkedList<>();
    private boolean sentidoHorario = true;

    // Componentes da Interface Gráfica
    private JPanel painelMesa;
    private JPanel painelMao;
    private JLabel lblStatus;
    private JLabel lblTopo;
    private JButton btnComprar;

    public UnoGUI() {
        super("Jogo UNO Gráfico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Ecrã de Configuração Inicial ---
        configurarPartidaGUI();
    }

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

    private void iniciarJogo() {
        inicializarBaralho();
        Collections.shuffle(baralho);
        prepararPartida();
        construirInterfacePrincipal();
        atualizarEcra();
    }

    private void construirInterfacePrincipal() {
    // Painel de Status (Topo)
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

    // MESA CENTRALIZADA: Usando GridBagLayout para alinhar tudo ao centro
    painelMesa = new JPanel(new GridBagLayout()); 
    painelMesa.setBackground(new Color(34, 139, 34)); // Verde feltro de cassino
    
    // Botão do Baralho (Compra)
    btnComprar = new JButton("Baralho (Comprar)");
    btnComprar.setPreferredSize(new Dimension(100, 150));
    btnComprar.setFont(new Font("Arial", Font.BOLD, 12));
    btnComprar.addActionListener(e -> comprarCartaGUI());
    
    add(painelMesa, BorderLayout.CENTER);

    // Painel da Mão do Jogador (Fundo)
    painelMao = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    painelMao.setBackground(new Color(60, 60, 60));
    JScrollPane scrollMao = new JScrollPane(painelMao);
    scrollMao.setPreferredSize(new Dimension(800, 170));
    add(scrollMao, BorderLayout.SOUTH);

    revalidate();
    repaint();
}

    // =====================================================================
    //  LÓGICA ADAPTADA DA CLASSE MESA
    // =====================================================================
    private void inicializarBaralho() {
    for (Carta.Cor cor : Carta.Cor.values()) {
        if (cor == Carta.Cor.PRETO) {
            // 4 Curingas Normais e 4 Curingas +4
            for (int i = 0; i < 4; i++) {
                baralho.add(new Carta(cor, Carta.Tipo.MUDA_COR, -1));
                baralho.add(new Carta(cor, Carta.Tipo.MAIS_QUATRO, -1));
            }
        } else {
            // Apenas UM número 0 por cor
            baralho.add(new Carta(cor, Carta.Tipo.NUMERO, 0));

            // DOIS números de 1 a 9 por cor
            for (int i = 1; i <= 9; i++) {
                baralho.add(new Carta(cor, Carta.Tipo.NUMERO, i));
                baralho.add(new Carta(cor, Carta.Tipo.NUMERO, i));
            }

            // DUAS cartas de ação por cor (+2, Inverter, Bloquear)
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

    // =====================================================================
    //  INTERAÇÃO COM A INTERFACE
    // =====================================================================
    private void atualizarEcra() {
    Jogador atual = jogadorAtual();
    Carta topoMesa = mesaDescarte.get(mesaDescarte.size() - 1);

    String direcao = sentidoHorario ? "➡️" : "⬅️";
    lblStatus.setText("Vez de: " + atual.getNome() + " | Sentido: " + direcao + " | Baralho: " + baralho.size() + " cartas");
    lblTopo.setText("Mesa Atual");

    // --- ATUALIZAÇÃO DA MESA (CENTRO) ---
    painelMesa.removeAll();
    
    // 1. Adiciona o botão de comprar (Baralho)
    painelMesa.add(btnComprar); 
    
    // 2. Adiciona um espaçamento horizontal de 30 pixels entre o Baralho e o Descarte
    painelMesa.add(Box.createHorizontalStrut(30)); 
    
    // 3. Cria o botão visual da carta que está no topo do descarte
    JButton btnTopo = criarBotaoCarta(topoMesa);
    btnTopo.setEnabled(false); // O topo é apenas para visualização, não deve ser clicável
    painelMesa.add(btnTopo);
    
    painelMesa.revalidate();
    painelMesa.repaint();

    // --- ATUALIZAÇÃO DA MÃO DO JOGADOR (INFERIOR) ---
    painelMao.removeAll();
    for (Carta carta : atual.getMao()) {
        JButton btnCarta = criarBotaoCarta(carta);
        boolean valida = jogadaEValida(carta, topoMesa);
        
        if (valida) {
            btnCarta.addActionListener(e -> jogarCarta(carta));
        } else {
            btnCarta.setEnabled(false); // Cartas inválidas ficam levemente apagadas/desativadas
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
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (carta.tipo != Carta.Tipo.NUMERO) {
                // 1. CARTAS ESPECIAIS: Tenta carregar a arte completa
                String caminhoImagem = "imagens/" + carta.cor.toString() + "_" + carta.tipo.toString() + ".png";
                ImageIcon imagemEspecial = new ImageIcon(caminhoImagem);
                
                if (imagemEspecial.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    g2.drawImage(imagemEspecial.getImage(), 0, 0, getWidth(), getHeight(), this);
                } else {
                    desenharCartaPorCodigo(g2, carta, getWidth(), getHeight()); // Plano B
                }
            } else {
                // 2. CARTAS DE NÚMERO: Tenta carregar apenas o fundo colorido genérico
                String caminhoFundo = "imagens/fundo_" + carta.cor.toString().toLowerCase() + ".png";
                ImageIcon iconeFundo = new ImageIcon(caminhoFundo);
                
                if (iconeFundo.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // Desenha o fundo e depois escreve os números por cima
                    g2.drawImage(iconeFundo.getImage(), 0, 0, getWidth(), getHeight(), this);
                    
                    g2.setColor(carta.cor == Carta.Cor.AMARELO ? Color.BLACK : Color.WHITE);
                    String simbolo = String.valueOf(carta.valor_numerico);
                    
                    // Número Gigante
                    g2.setFont(new Font("Arial", Font.BOLD, 45));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(simbolo, (getWidth() - fm.stringWidth(simbolo)) / 2, (getHeight() / 2) + (fm.getAscent() / 3));
                    
                    // Números pequenos nos cantos
                    g2.setFont(new Font("Arial", Font.BOLD, 14));
                    g2.drawString(simbolo, 8, 20);
                    g2.translate(getWidth() - 8, getHeight() - 8);
                    g2.rotate(Math.PI);
                    g2.drawString(simbolo, 0, 4);
                    g2.rotate(-Math.PI);
                    g2.translate(-(getWidth() - 8), -(getHeight() - 8));
                } else {
                    desenharCartaPorCodigo(g2, carta, getWidth(), getHeight()); // Plano B
                }
            }

            // Aplica transparência se a carta não puder ser jogada
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

// Método auxiliar que desenha a carta inteira caso seja número (ou caso falte a imagem da especial)
private void desenharCartaPorCodigo(Graphics2D g2, Carta carta, int largura, int altura) {
    // Fundo da carta
    g2.setColor(obterCorJava(carta.cor));
    g2.fillRoundRect(0, 0, largura, altura, 16, 16);
    
    // Borda branca fininha
    g2.setColor(Color.WHITE);
    g2.setStroke(new BasicStroke(3));
    g2.drawRoundRect(2, 2, largura - 4, altura - 4, 16, 16);

    // Elipse central branca (só desenha se não for a carta PRETA)
    if (carta.cor != Carta.Cor.PRETO) {
        g2.rotate(-Math.PI / 6, largura / 2.0, altura / 2.0);
        g2.fillOval(10, 25, largura - 20, altura - 50);
        g2.rotate(Math.PI / 6, largura / 2.0, altura / 2.0);
    }

    // Define cor do texto (Preto para o centro, branco para os cantos)
    String simbolo = obterSimboloDaCarta(carta);
    
    // Texto Central (Gigante)
    g2.setColor(Color.BLACK);
    if(carta.cor == Carta.Cor.PRETO) g2.setColor(Color.WHITE); // Coringas sem imagem ficam com texto branco
    g2.setFont(new Font("Arial", Font.BOLD, 45));
    FontMetrics fm = g2.getFontMetrics();
    int xCentro = (largura - fm.stringWidth(simbolo)) / 2;
    int yCentro = (altura / 2) + (fm.getAscent() / 3);
    g2.drawString(simbolo, xCentro, yCentro);

    // Texto Canto Superior Esquerdo
    g2.setColor(Color.WHITE);
    if (carta.cor == Carta.Cor.AMARELO) g2.setColor(Color.BLACK); // Contraste para o fundo amarelo
    g2.setFont(new Font("Arial", Font.BOLD, 14));
    g2.drawString(simbolo, 8, 20);

    // Texto Canto Inferior Direito (Invertido)
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
        case INVERTER: return "↺";
        case BLOQUEAR: return "Ø";
        case MUDA_COR: return "🎨";
        default: return "";
    }
}

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
                JOptionPane.showMessageDialog(this, "PAAA!!! " + jogadorAtual().getNome() + " foi bloqueado e perde a vez!");
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
            default: return Carta.Cor.VERMELHO; // Prevenção se o utilizador fechar a janela
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
                "Comprou a carta: " + comprada.toString() + ".\nEsta carta é válida! Deseja jogá-la agora?", 
                "Carta Comprada", JOptionPane.YES_NO_OPTION);
            
            if (resposta == JOptionPane.YES_OPTION) {
                atual.removerCarta(comprada);
                mesaDescarte.add(comprada);
                if (verificarVitoria(atual)) return;
                processarEfeitoCartaGUI(comprada);
                return; // O ecrã já foi atualizado na função acima
            }
        } else {
            JOptionPane.showMessageDialog(this, "Comprou a carta: " + comprada.toString() + ".\nEla não serve para jogar. Passando a vez...");
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
            JOptionPane.showMessageDialog(this, "VITÓRIA! 🎉\n" + jogador.getNome() + " venceu o jogo de Uno!");
            System.exit(0);
            return true;
        }
        if (jogador.getQuantidadeCartas() == 1) {
            JOptionPane.showMessageDialog(this, "⚠️ " + jogador.getNome() + " grita: UNO!!!");
        }
        return false;
    }

    // =====================================================================
    //  MAIN
    // =====================================================================
    public static void main(String[] args) {
        // Altera o design da janela para o padrão do sistema operativo
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            UnoGUI jogo = new UnoGUI();
            jogo.setVisible(true);
        });
    }
    
}