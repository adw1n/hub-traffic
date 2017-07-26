import React from 'react';
import ReactDOM from 'react-dom';



import createPlotlyComponent from 'react-plotlyjs';
// import Plotly from 'plotly.js/dist/plotly.js';
const PlotlyComponent = createPlotlyComponent(Plotly);
function getNthDayDate(date, nthDay){
    return new Date(new Date(date).setDate(date.getDate()+nthDay))
}
function getNextDayDate(date){
    return getNthDayDate(date, 1)
}
function differenceInDays(date1, date2){
    return Math.round(Math.abs((date1-date2))/(1000*60*60*24));
}

const GitHubTrafficAPIDays = 14;
const SmallGraphThresholdDays = 30;



class GitHubRepositoryViewsChart extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            id: this.props.name+"-visits"
        };
        this.chartTitle="Visitors";
        this.uniqueLineTitle="unique visitors";
        this.totalLineTitle="visits";
        this.getDateStr = this.getDateStr.bind(this);
        this.getChartValues = this.getChartValues.bind(this);
    }

    getDateStr(date){
        let monthNo = date.getMonth()+1;
        return ((monthNo>=10 ? monthNo : "0"+monthNo)
        +"/"
        +(date.getDate()>=10 ? date.getDate() : "0"+date.getDate()));
    }


    getChartValues(values){
        let dates = values.map(value => new Date(value.timestamp));
        let allValues = values.map(value => {
            return {date: new Date(value.timestamp), count: value.count, uniques: value.uniques}
        });


        const minDate = dates.length ? new Date(Math.min.apply(null,dates)) : new Date();
        const maxDate = dates.length ? new Date(Math.max.apply(null,dates)) : new Date();



        // TODO min(..., userJoinDate)
        const startDate = new Date(
            Math.min(
                minDate ? minDate : new Date(),
                getNthDayDate(new Date(), -GitHubTrafficAPIDays)
            )
        );
        const endDate = new Date(Math.max(maxDate ? maxDate : new Date(), new Date()));

        let date = startDate;
        while(date < endDate){
            if(!dates.find(item => item.toDateString()===date.toDateString())){ // test if date with same day already exists
                allValues.push({date: new Date(date), count: 0, uniques: 0})
            }
            date=getNextDayDate(date);
        }
        allValues.sort((val1, val2) => val1.date.getTime()-val2.date.getTime());
        return (
        {   startDate, endDate,
            values: allValues.map(value => {
                return {date: this.getDateStr(value.date), count: value.count, uniques: value.uniques}
            })
        });
    }

    render(){
        let values = this.getChartValues(this.props.values);

        let totalCount = {
            x: values.values.map(val => val.date),
            y: values.values.map(val => val.count),
            mode: 'lines',
            marker: {
                color: 'blue',
                size: 8
            },
            line: {
                color: 'blue',
                width: 1
            },
            name: this.totalLineTitle
        };

        let uniqueCount = {
            x: values.values.map(val => val.date),
            y: values.values.map(val => val.uniques),
            mode: 'lines',
            marker: {
                color: 'green',
                size: 8
            },
            line: {
                color: 'green',
                width: 1
            },
            name: this.uniqueLineTitle,
            yaxis: 'y2'
        };
        let layout = {
            title: this.chartTitle,
            yaxis: { //https://plot.ly/javascript/multiple-axes/
                side: 'left',
                color: 'blue'
            },
            yaxis2: {
                overlaying: 'y',
                side: 'right',
                color: 'green'
            }
        };
        let data = [ totalCount, uniqueCount];
        let timePeriodInDays = differenceInDays(values.startDate, values.endDate);
        return (
            <div className={"col-sm-12 col-lg-"+(timePeriodInDays>SmallGraphThresholdDays ? 12 : 6)}>
                <PlotlyComponent data={data} layout={layout} />
            </div>
        );
    }
}
class GitHubRepositoryClonesChart extends GitHubRepositoryViewsChart{
    constructor(props){
        super(props);
        this.state = {
            id: this.props.name+"-clones"
        };
        this.chartTitle="Git clones";
        this.uniqueLineTitle="unique cloners";
        this.totalLineTitle="clones"
    }

}

class GitHubRepositoryChart extends React.Component{
    render(){
        return (
            <div className="row">
                <div className="col-lg-12"><h4>{this.props.name}</h4></div>
                <GitHubRepositoryViewsChart name={this.props.name} values={this.props.views}/>
                <GitHubRepositoryClonesChart name={this.props.name} values={this.props.clones}/>
            </div>
        )
    }
}

class LoginHeader extends React.Component{

}


function getRandomInt(minimum, maximum) {
    return Math.floor(Math.random() * (maximum - minimum + 1)) + minimum;
}

function getDemoChartData(startDate, endDate, maximumViews, maximumUniqueViews, maximumClones, maximumUniqueClones){
    console.assert(startDate.getTime()<=endDate.getTime());
    let date = new Date(startDate);
    let views = [];
    let clones = [];
    while(date <= endDate){
        let count = getRandomInt(0, maximumViews);
        views.push({
            timestamp: date.getTime(),
            count,
            uniques: getRandomInt(count > 0 ? 1 : 0, Math.min(maximumUniqueViews, count))
        });
        count = getRandomInt(0, maximumClones);
        clones.push({
            timestamp: date.getTime(),
            count,
            uniques: getRandomInt(count > 0 ? 1 : 0, Math.min(maximumUniqueClones, count))
        });
        date = getNextDayDate(date);
    }
    return {views, clones};
}

class DemoExample extends React.Component{
    constructor(props){
        super(props);
        const endDate = new Date();
        let demoRepoNames = [
            {name: "dns-spoofer", startDate: new Date("Sun Jan 01 2017 02:00:00 GMT+0200 (CEST)")},
            {name: "hub-traffic", startDate: getNthDayDate(endDate, -20)},
            {name: "audio-gallery", startDate: getNthDayDate(endDate, -40)}
        ];
        this.demoRepos=demoRepoNames.map(repo => {
            let repoData = getDemoChartData(
                repo.startDate,
                endDate ? endDate : console.assert("fucked up"),
                100,
                10,
                50,
                9
            );
            repoData.name = repo.name;
            return repoData;
        });
    }
    render(){
        const repos = this.demoRepos.map(repo => <GitHubRepositoryChart key={repo.name}
                                                                        name={repo.name}
                                                                        views={repo.views}
                                                                        clones={repo.clones}/>
        );
        return (
            <div>
                <h2 className="text-center">DEMO</h2>
                <p>user: JohnDoe</p>
                {repos}
            </div>
        );
    }
}

class Repos extends React.Component{
    constructor(props){
        super(props);
        this.state={
            user: null,
            repositories: []
        }
    }
    componentDidMount(){
        $.get("/api/githubUser", data => {
            this.setState({
                user: data
            });
            console.log(data);
        }).done(()=>{
            $.get("/api/repository/traffic",data =>{
                console.log(data);
                this.setState({
                    repositories: data
                })
            })
        })
    }
    render(){
        const user = this.state.user;
        console.log("user: ");
        console.log(user);
        console.log("repositories: ");
        console.log(this.state.repositories);
        const repositories = this.state.repositories.map(repo=>{
            return <GitHubRepositoryChart key={repo.name.name}
                                          name={repo.name.name}
                                          views={repo.views ? repo.views : []}
                                          clones={repo.clones ? repo.clones : []} />
        });
        return (
            <div>
                <p>user: {user ? user.name : ""}</p>
                {repositories.length ? repositories : (
                    <div>
                        <i className="fa fa-refresh fa-spin fa-3x fa-fw" style={{position: "relative", left: "50%"}}></i>
                        <span className="sr-only">Loading...</span>
                    </div>
                )}
            </div>
        )
    }
}

class Page extends React.Component{
    render(){
        return currentUser=="anonymousUser" ? <DemoExample/> : <Repos/>
    }
}



class UnregisterButton extends React.Component{
    constructor(props){
        super(props);
        this.unregister=this.unregister.bind(this);
    }
    unregister(){
        console.log("unregister clicked");
        $.ajax({
            method: "DELETE",
            url: "/api/user"
        }).done(()=>{
            console.log("done -> logout");
            window.location.href="/logout"
        }).fail(()=>{
            console.log("fail");
            // TODO display error msg in red - ERROR: failed to unregister
        })
    }
    componentDidMount(){
        $("#unregisterButton").click(this.unregister);
    }
    render(){
        return (
            <a className="nav-link page-scroll" data-toggle="modal" data-target="#myModal" href="#myModal">
                Unregister
            </a>
        )
    }
}

class NavigationButtons extends React.Component{
    render(){
        return this.props.logged ? (
            <ul className="navbar-nav ml-auto">
                <li className="nav-item">
                    <a className="nav-link page-scroll" href="/logout">logout</a>
                </li>
                <li className="nav-item">
                    <UnregisterButton />
                </li>
            </ul>
        ) : (
            <ul className="navbar-nav ml-auto">
                <li className="nav-item">
                    <a className="nav-link page-scroll" href="#demo">demo</a>
                </li>
                <li className="nav-item">
                    <a className="nav-link page-scroll" href="/login">login</a>
                </li>
            </ul>
        )
    }
}

// ========================================

ReactDOM.render(
    <NavigationButtons logged={currentUser=="anonymousUser" ? false : true}/>,
    document.getElementById('navbarExample')
);

ReactDOM.render(
    <Page/>,
    document.getElementById('react')
);
