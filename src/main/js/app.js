import React from 'react';
import ReactDOM from 'react-dom';



import createPlotlyComponent from 'react-plotlyjs';
const PlotlyComponent = createPlotlyComponent(Plotly);

class GitHubRepositoryViewsChart extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            id: this.props.name+"-visits"
        };
        this.chartTitle="Visitors";
    }
    render(){
        let dates=[];
        let totalCountYAxis=[];
        let uniqueCountYAxis=[];
        this.props.values.forEach((value) => {
            let date = new Date(value.timestamp);
            dates.push((date.getMonth()+1)+"/"+date.getDate());
            totalCountYAxis.push(value.count);
            uniqueCountYAxis.push(value.uniques);
        });

        //TODO add (0,0) vals between startDate and endDate

        let totalCount = {
            x: dates,
            y: totalCountYAxis,
            mode: 'lines+markers',
            marker: {
                color: 'blue',
                size: 8
            },
            line: {
                color: 'blue',
                width: 1
            },
            name: "visits"
        };
        let uniqueCount = {
            x: dates,
            y: uniqueCountYAxis,
            mode: 'lines+markers',
            marker: {
                color: 'green',
                size: 8
            },
            line: {
                color: 'green',
                width: 1
            },
            name: "unique visitors",
            yaxis: 'y2'
        };
        let layout = {
            title: this.chartTitle,
            yaxis: { //https://plot.ly/javascript/multiple-axes/
                side: 'left',
                color: 'green'
            },
            yaxis2: {
                overlaying: 'y',
                side: 'right',
                color: 'blue'
            }
        };
        let data = [ totalCount, uniqueCount];
        return (
            <div className="col-lg-6">
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

class DemoExample extends React.Component{
    constructor(props){
        super(props);
        this.audio_gallery_repo_visits = {
            name: "hub-traffic",
            views: [
                {
                    "timestamp": 1499558400000,
                    "count": 3,
                    "uniques": 1
                },
                {
                    "timestamp": 1499644800000,
                    "count": 3,
                    "uniques": 1
                },
                {
                    "timestamp": 1499904000000,
                    "count": 4,
                    "uniques": 1
                },
                {
                    "timestamp": 1500508800000,
                    "count": 13,
                    "uniques": 2
                }
            ],
            clones: [
                {
                    "timestamp": 1499558400000,
                    "count": 4,
                    "uniques": 2
                },
                {
                    "timestamp": 1499644800000,
                    "count": 3,
                    "uniques": 1
                },
                {
                    "timestamp": 1499904000000,
                    "count": 0,
                    "uniques": 0
                },
                {
                    "timestamp": 1500508800000,
                    "count": 5,
                    "uniques": 3
                }
            ],
        };
    }
    render(){
        return (
            <GitHubRepositoryChart name={this.audio_gallery_repo_visits.name}
                                   views={this.audio_gallery_repo_visits.views}
                                   clones={this.audio_gallery_repo_visits.clones}/>
        )
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
                {user ?
                    (<div>user: {user.name}</div>)
                    : (<div></div>)
                }
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