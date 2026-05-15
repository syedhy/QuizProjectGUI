package com.quizapp.network;

import java.io.IOException;
import java.util.List;

import com.quizapp.helpers.Question;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class PhoneQuizServer {

    private HttpServer server;

    private final PhoneQuizState state;

    public PhoneQuizServer(List<Question> questions) {
        this.state = new PhoneQuizState(questions);
    }

    public PhoneQuizState getState() {
        return state;
    }

    public void start(int port) throws IOException {

        server = HttpServer.create(
                new java.net.InetSocketAddress("0.0.0.0", port),
                0);

        server.createContext("/", this::handleHome);
        server.createContext("/answer", this::handleAnswer);

        server.setExecutor(null);
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public String getServerUrl(int port) throws Exception {
        java.net.DatagramSocket socket = new java.net.DatagramSocket();

        socket.connect(
                java.net.InetAddress.getByName("8.8.8.8"),
                80);

        String ip = socket.getLocalAddress().getHostAddress();

        socket.close();

        return "http://" + ip + ":" + port;
    }

    private void handleHome(HttpExchange exchange) throws IOException {
        String html;

        try {
            if (state.getTotalQuestions() == 0) {
                html = "<h1>No questions loaded</h1>";
            } else if (state.isFinished()) {
                html = buildResultPage();
            } else {
                Question q = state.getCurrentQuestion();

                if (q == null) {
                    html = "<h1>No current question found</h1>";
                } else {
                    html = buildQuestionPage(q);
                }
            }

            byte[] response = html.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add(
                    "Content-Type",
                    "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();

        } catch (Exception e) {
            String error = "<h1>Server Error</h1><pre>" + e.getMessage() + "</pre>";
            byte[] response = error.getBytes(java.nio.charset.StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(500, response.length);
            exchange.getResponseBody().write(response);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }

    private void handleAnswer(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();

        String answer = "A";

        if (query != null && query.startsWith("choice=")) {
            answer = query.substring("choice=".length());
        }

        state.submitAnswer(answer);

        exchange.getResponseHeaders().add(
                "Location",
                "/");

        exchange.sendResponseHeaders(302, -1);
        exchange.getResponseBody().close();
    }

    private String buildQuestionPage(Question q) {

        return """
                <!DOCTYPE html>
                <html>
                <head>

                <meta name="viewport" content="width=device-width, initial-scale=1.0">

                <title>Phone Quiz</title>

                <style>

                *{
                    box-sizing:border-box;
                }

                body{
                    min-height:100vh;
                    margin:0;
                    padding:22px;
                    color:white;
                    font-family:-apple-system , BlinkMacSystemFont , "Segoe UI" , Arial , sans-serif;
                    background:
                        radial-gradient(circle at 10%% 20%% , rgba(34,211,238,0.22) , transparent 32%%),
                        radial-gradient(circle at 90%% 12%% , rgba(139,92,246,0.24) , transparent 34%%),
                        radial-gradient(circle at 78%% 88%% , rgba(236,72,153,0.18) , transparent 36%%),
                        linear-gradient(135deg , #020617 , #090b22 45%% , #111827);
                }

                .wrapper{
                    width:100%%;
                    max-width:720px;
                    margin:0 auto;
                }

                .top{
                    display:flex;
                    justify-content:space-between;
                    align-items:center;
                    margin-bottom:18px;
                    color:rgba(255,255,255,0.72);
                    font-size:14px;
                    font-weight:700;
                }

                .card{
                    background:rgba(255,255,255,0.075);
                    border:1px solid rgba(255,255,255,0.16);
                    box-shadow:0 24px 70px rgba(0,0,0,0.38);
                    border-radius:30px;
                    padding:24px;
                    backdrop-filter:blur(20px);
                }

                .badge{
                    display:inline-block;
                    color:#67E8F9;
                    border:1px solid rgba(34,211,238,0.40);
                    background:rgba(34,211,238,0.12);
                    padding:9px 16px;
                    border-radius:999px;
                    font-size:13px;
                    font-weight:900;
                    letter-spacing:0.08em;
                    margin-bottom:18px;
                }

                h1{
                    font-size:28px;
                    line-height:1.25;
                    margin:0 0 22px 0;
                }

                button{
                    width:100%%;
                    margin-top:14px;
                    padding:18px 18px;
                    border:1px solid rgba(255,255,255,0.16);
                    border-radius:22px;
                    font-size:17px;
                    line-height:1.35;
                    font-weight:800;
                    text-align:left;
                    color:white;
                    background:rgba(255,255,255,0.075);
                    box-shadow:0 10px 30px rgba(0,0,0,0.18);
                }

                button:active{
                    transform:scale(0.98);
                    background:rgba(34,211,238,0.18);
                    border-color:rgba(34,211,238,0.62);
                }

                .progress-shell{
                    height:12px;
                    width:100%%;
                    background:rgba(255,255,255,0.10);
                    border-radius:999px;
                    overflow:hidden;
                    margin-bottom:20px;
                }

                .progress-bar{
                    height:100%%;
                    width:%s%%;
                    background:linear-gradient(90deg , #22D3EE , #8B5CF6 , #EC4899);
                    border-radius:999px;
                }

                .footer{
                    text-align:center;
                    margin-top:18px;
                    color:rgba(255,255,255,0.55);
                    font-size:13px;
                }

                </style>

                </head>

                <body>

                <div class="wrapper">

                <div class="top">
                <div>Phone Quiz</div>
                <div>Question %d / %d</div>
                </div>

                <div class="progress-shell">
                <div class="progress-bar"></div>
                </div>

                <div class="card">

                <div class="badge">LIVE QUIZ</div>

                <h1>%s</h1>

                <form action="/answer">
                <button name="choice" value="A">A. %s</button>
                <button name="choice" value="B">B. %s</button>
                <button name="choice" value="C">C. %s</button>
                <button name="choice" value="D">D. %s</button>
                </form>

                </div>

                <div class="footer">
                Answer on your phone , results appear on laptop
                </div>

                </div>

                </body>
                </html>
                """
                .formatted(
                        ((state.getCurrentIndex() + 1) * 100.0) / state.getTotalQuestions(),
                        state.getCurrentIndex() + 1,
                        state.getTotalQuestions(),
                        escape(q.question),
                        escape(q.options[0]),
                        escape(q.options[1]),
                        escape(q.options[2]),
                        escape(q.options[3]));
    }

    private String buildResultPage() {

        int correct = state.getCorrectAnswers();
        int total = state.getTotalQuestions();
        int wrong = state.getWrongAnswers();

        double accuracy = total == 0 ? 0 : ((double) correct / total) * 100;

        return """
                <!DOCTYPE html>
                <html>
                <head>

                <meta name="viewport" content="width=device-width, initial-scale=1.0">

                <title>Quiz Result</title>

                <style>

                *{
                    box-sizing:border-box;
                }

                body{
                    min-height:100vh;
                    margin:0;
                    padding:24px;
                    color:white;
                    font-family:-apple-system , BlinkMacSystemFont , "Segoe UI" , Arial , sans-serif;
                    display:flex;
                    align-items:center;
                    justify-content:center;
                    background:
                        radial-gradient(circle at 15%% 18%% , rgba(34,211,238,0.24) , transparent 32%%),
                        radial-gradient(circle at 85%% 20%% , rgba(139,92,246,0.25) , transparent 34%%),
                        radial-gradient(circle at 70%% 88%% , rgba(236,72,153,0.20) , transparent 36%%),
                        linear-gradient(135deg , #020617 , #090b22 45%% , #111827);
                }

                .card{
                    width:100%%;
                    max-width:680px;
                    text-align:center;
                    background:rgba(255,255,255,0.075);
                    border:1px solid rgba(255,255,255,0.16);
                    box-shadow:0 24px 70px rgba(0,0,0,0.38);
                    border-radius:34px;
                    padding:34px 24px;
                    backdrop-filter:blur(20px);
                }

                .badge{
                    display:inline-block;
                    color:#67E8F9;
                    border:1px solid rgba(34,211,238,0.40);
                    background:rgba(34,211,238,0.12);
                    padding:9px 16px;
                    border-radius:999px;
                    font-size:13px;
                    font-weight:900;
                    letter-spacing:0.08em;
                    margin-bottom:18px;
                }

                h1{
                    font-size:30px;
                    margin:0 0 14px 0;
                }

                .score{
                    font-size:72px;
                    font-weight:900;
                    margin:14px 0;
                    background:linear-gradient(90deg , #22D3EE , #8B5CF6 , #EC4899);
                    -webkit-background-clip:text;
                    color:transparent;
                }

                .stats{
                    display:grid;
                    grid-template-columns:1fr 1fr 1fr;
                    gap:12px;
                    margin-top:24px;
                }

                .stat{
                    background:rgba(255,255,255,0.07);
                    border:1px solid rgba(255,255,255,0.14);
                    border-radius:20px;
                    padding:16px 10px;
                }

                .value{
                    font-size:22px;
                    font-weight:900;
                }

                .label{
                    font-size:12px;
                    color:rgba(255,255,255,0.58);
                    margin-top:6px;
                    font-weight:800;
                }

                .footer{
                    margin-top:24px;
                    color:rgba(255,255,255,0.58);
                    font-size:14px;
                }

                </style>

                </head>

                <body>

                <div class="card">

                <div class="badge">QUIZ COMPLETE</div>

                <h1>Your Result</h1>

                <div class="score">
                %d / %d
                </div>

                <div class="stats">

                <div class="stat">
                <div class="value">%.1f %%</div>
                <div class="label">Accuracy</div>
                </div>

                <div class="stat">
                <div class="value">%d</div>
                <div class="label">Correct</div>
                </div>

                <div class="stat">
                <div class="value">%d</div>
                <div class="label">Wrong</div>
                </div>

                </div>

                <div class="footer">
                You can now check the final result on your laptop
                </div>

                </div>

                </body>
                </html>
                """
                .formatted(
                        correct,
                        total,
                        accuracy,
                        correct,
                        wrong);
    }

    private String escape(String text) {
        if (text == null)
            return "";

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}