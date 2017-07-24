import React from 'react';
import ReactDOM from 'react-dom';



import createPlotlyComponent from 'react-plotlyjs';
// import Plotly from 'plotly.js/dist/plotly.js';
const PlotlyComponent = createPlotlyComponent(Plotly);
function getNextDayDate(date){
    return new Date(date.setDate(date.getDate()+1))
}
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


        const minDate = new Date(Math.min.apply(null,dates));
        const maxDate = new Date(Math.max.apply(null,dates));


        let date = minDate;
        while(date < maxDate){
            if(!dates.find(item => item.getTime()==date.getTime())){
                allValues.push({date: new Date(date), count: 0, uniques: 0})
            }
            date=getNextDayDate(date);
        }
        allValues.sort((val1, val2) => val1.date.getTime()-val2.date.getTime());
        return allValues.map(value => {
            return {date: this.getDateStr(value.date), count: value.count, uniques: value.uniques}
        });
    }

    render(){
        // TODO startDate is max(userRegistrationToHubTrafficDate, 14 days) //14 days cuz this is what github gives us
        let values = this.getChartValues(this.props.values);

        let totalCount = {
            x: values.map(val => val.date),
            y: values.map(val => val.count),
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
            x: values.map(val => val.date),
            y: values.map(val => val.uniques),
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
        return (
            <PlotlyComponent data={data} layout={layout} />
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
                <div className="col-lg-12">
                    <GitHubRepositoryViewsChart name={this.props.name} values={this.props.views}/>
                </div>
                <div className="col-lg-12">
                    <GitHubRepositoryClonesChart name={this.props.name} values={this.props.clones}/>
                </div>
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
        let demoRepoNames = ["dns-spoofer", "hub-traffic", "audio-gallery"];
        this.demoRepos=demoRepoNames.map(repoName => {
            let repo = getDemoChartData(
                new Date("Sun Jan 01 2017 02:00:00 GMT+0200 (CEST)"),
                new Date(),
                100,
                10,
                50,
                9
            );
            repo.name = repoName;
            return repo;
        });
    }
    render(){
        const repos = this.demoRepos.map(repo => <GitHubRepositoryChart key={repo.name}
                                                                        name={repo.name}
                                                                        views={repo.views}
                                                                        clones={repo.clones}/>
        );
        return (
            <div>{repos}</div>
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
            $.get("/api/userRepositories",data =>{
                console.log(data);
                this.setState({
                    repositories: data
                })
            }).done(()=>{
                this.state.repositories.forEach(repository=>{
                    $.get("/api/repository/views/"+repository.name, data=>{
                        const repositories = this.state.repositories;
                        // TODO use update immutability helper instead
                        for(let i=0; i<repositories.length; ++i){
                            if(repositories[i].name == repository.name)
                                repositories[i].views=data;
                        }
                        this.forceUpdate()
                    });
                    $.get("/api/repository/clones/"+repository.name, data=>{
                        const repositories = this.state.repositories;
                        for(let i=0; i<repositories.length; ++i){
                            if(repositories[i].name == repository.name)
                                repositories[i].clones=data;
                        }
                        this.forceUpdate()
                    });
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
            return <GitHubRepositoryChart key={repo.name}
                                          name={repo.name}
                                          views={repo.views ? repo.views : []}
                                          clones={repo.clones ? repo.clones : []} />
        });
        return (
            <div>
                <div>user: {user ? user.name : "JohnDoe"}</div>
                {repositories}
            </div>
        )
    }
}

class Page extends React.Component{
    render(){
        return currentUser=="anonymousUser" ? <DemoExample/> : <Repos/>
    }
}


// ========================================



ReactDOM.render(
    <Page/>,
    document.getElementById('react')
);