// plotly.newplot
// jquery-3.2.1.min.js

function graphplot() {
    var allLines = [];

    var wx = ACTIVITY_DATA.getRunX().split("::");
    var wy = ACTIVITY_DATA.getRunY().split("::");
    var wz = ACTIVITY_DATA.getRunZ().split("::");

    for (i = 0; i < wx.length; i++) {
        var x = wx[i].split(",");
        var y = wy[i].split(",");
        var z = wz[i].split(",");

        var line = {
            x: x,
            y: y,
            z: z,
            mode: 'lines',
            marker: {
                color: 'cyan',
                size: 12,
                symbol: 'circle',
                line: {
                    color: 'rgb(0,0,0)',
                    width: 0
                }
            },
            line: {
                color: 'cyan',
                width: 1
            },
            type: 'scatter3d',
            name: "run",
            showlegend: false
        };
        allLines.push(line);
    }

    var wx = ACTIVITY_DATA.getWalkX().split("::");
    var wy = ACTIVITY_DATA.getWalkY().split("::");
    var wz = ACTIVITY_DATA.getWalkZ().split("::");

    for (i = 0; i < wx.length; i++) {
        var x = wx[i].split(",");
        var y = wy[i].split(",");
        var z = wz[i].split(",");

        var line = {
            x: x,
            y: y,
            z: z,
            mode: 'lines',
            marker: {
                color: 'green',
                size: 12,
                symbol: 'circle',
                line: {
                    color: 'rgb(0,0,0)',
                    width: 0
                }
            },
            line: {
                color: 'green',
                width: 1
            },
            type: 'scatter3d',
            name: "walk",
            showlegend: false
        };
        allLines.push(line);
    }

    var wx = ACTIVITY_DATA.getJumpX().split("::");
    var wy = ACTIVITY_DATA.getJumpY().split("::");
    var wz = ACTIVITY_DATA.getJumpZ().split("::");

    for (i = 0; i < wx.length; i++) {
        var x = wx[i].split(",");
        var y = wy[i].split(",");
        var z = wz[i].split(",");

        var line = {
            x: x,
            y: y,
            z: z,
            mode: 'lines',
            marker: {
                color: 'red',
                size: 12,
                symbol: 'circle',
                line: {
                    color: 'rgb(0,0,0)',
                    width: 0
                }
            },
            line: {
                color: 'red',
                width: 1
            },
            type: 'scatter3d',
            name: "jump",
            showlegend: false
        };
        allLines.push(line);
    }

    var frame = {
        autosize: true,

        margin: {
            l: 0,
            r: 0,
            b: 0,
            t: 0
        },
        scene: {
            xaxis: { title: 'X AXIS' },
            yaxis: { title: 'Y AXIS' },
            zaxis: { title: 'Z AXIS' },
        }
    };

    Plotly.newPlot('graph', allLines, frame, {displayModeBar: true});
}
