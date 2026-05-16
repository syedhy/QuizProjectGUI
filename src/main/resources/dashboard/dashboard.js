document.querySelectorAll("[data-width]").forEach(element => {
    const width = Number(element.dataset.width || 0);
    element.style.width = `${Math.min(100 , Math.max(0 , width))}%`;
});
const chartData = [
    Number("{{timedGames}}"),
    Number("{{survivalGames}}"),
    Number("{{suddenDeathGames}}"),
    Number("{{pvpGames}}"),
    Number("{{llmGames}}"),
    Number("{{eloGames}}")
];

const safeChartData = chartData.every(value => value === 0)
    ? [1 , 1 , 1 , 1 , 1 , 1]
    : chartData;

const labels = ["Timed" , "Survival" , "Sudden Death" , "PvP" , "LLM" , "ELO"];

const colors = ["#06b6d4" , "#10b981" , "#7c3aed" , "#f97316" , "#e879f9" , "#38bdf8"];

new Chart(document.getElementById("modeChart") , {
    type: "doughnut" ,
    data: {
        labels: labels ,
        datasets: [{
            data: safeChartData ,
            backgroundColor: colors ,
            borderColor: "#020617" ,
            borderWidth: 6 ,
            hoverOffset: 10
        }]
    } ,
    options: {
        maintainAspectRatio: false ,
        cutout: "64%" ,
        plugins: {
            legend: {
                position: "bottom" ,
                labels: {
                    color: "#e5e7eb" ,
                    padding: 18 ,
                    font: {
                        size: 13 ,
                        weight: "bold"
                    }
                }
            }
        }
    }
});