'use strict';


import React from 'react';
// import renderer from 'react-test-renderer';
// import { jsdom } from 'jsdom';
// require('jsdom-global')()
// import GitHubRepositoryViewsChart from '../app.react';


function getDateStr(date){
    let monthNo = date.getMonth()+1;
    return ((monthNo>=10 ? monthNo : "0"+monthNo)
        +"/"
        +(date.getDate()>=10 ? date.getDate() : "0"+date.getDate()));
}

function getNextDayDate(date){
    return new Date(date.setDate(date.getDate()+1))
}

function getChartValues(values){

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
        return {date: getDateStr(value.date), count: value.count, uniques: value.uniques}
    });
}

test('test getDateStr', ()=>{
    expect(getDateStr(new Date(1499558400000))).toEqual("07/09")
});

test('test getChartValues', () => {
    let testValues = [
        {
            "timestamp": 1499558400000,
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
    ];
    var expectedVals = [
        {
            date: "07/09",
            count: 3,
            uniques: 1
        },
        {
            date: "07/10",
            count: 0,
            uniques: 0
        },
        {
            date: "07/11",
            count: 0,
            uniques: 0
        },
        {
            date: "07/12",
            count: 0,
            uniques: 0
        },
        {
            date: "07/13",
            count: 4,
            uniques: 1
        },

        {
            date: "07/14",
            count: 0,
            uniques: 0
        },
        {
            date: "07/15",
            count: 0,
            uniques: 0
        },
        {
            date: "07/16",
            count: 0,
            uniques: 0
        },
        {
            date: "07/17",
            count: 0,
            uniques: 0
        },
        {
            date: "07/18",
            count: 0,
            uniques: 0
        },
        {
            date: "07/19",
            count: 0,
            uniques: 0
        },
        {
            date: "07/20",
            count: 13,
            uniques: 2
        }
    ];
    expect(getChartValues(testValues)).toEqual(expectedVals);
});

test('test getChartValues on empty values', () => {
    expect(getChartValues([])).toEqual([]);
});