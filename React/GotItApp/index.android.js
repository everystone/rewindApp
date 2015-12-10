/**
 * GotIt React Client
 * EIrik Kvarstein
 */
'use strict';

var React = require('react-native');
var DDP = require('ddp.js');
var Toolbar = require('./components/Toolbar.js');
var Input = require('./components/Input.js');
var LectureSelector = require('./components/LectureSelector.js');
var QuestionList = require('./components/QuestionList.js');

var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
} = React;



var GotItApp = React.createClass({
  getInitialState: function(){
    var options = {
        endpoint: "http://192.168.11.87:3000/websocket",
        SocketConstructor: WebSocket
    };
    return{
        ddp: new DDP(options),
        status: "idle..",
        message: '',
        subId_questions: '',
        subId_votes: '',
        subId_lectures: '',
        questions: {},
        lectures: {},
        inLecture: false,
        currentLectureCode: '',



    };
  },
  onConnected: function(){
    this.setState({status: "Connected"});
    this._login();

  },
  onAdded: function(data){
    this.setState({message: data.collection});

    switch(data.collection){
      case "lectures":
        var _lectures = this.state.lectures;
        _lectures[data.id] = data.fields;
          this.setState({
            lectures: _lectures
          });
        break;
      case "questions":
      var _questions = this.state.questions;
      data.fields.votes = 0;
      _questions[data.id] = data.fields;
        this.setState({
          questions: _questions,
          status: data.id,
        });
        break;

      case "votes":
      // BUG: setState() on dataSource does not trigger render() in child
      var _questions = this.state.questions;
      console.log("Adding vote for "+_questions[data.fields.questionId]);
      _questions[data.fields.questionId].votes++;

      this.setState({
        questions: _questions,
      });
        break;
    }
    //collection, id, fields
    // questions should be a hashmap<id, data>. map.put(data.id, data)

  },
  onChanged: function(data){

  },
  onRemoved: function(data){

  },
  onResult: function(data){
    console.log("Result: ",data);
  },
  _enterLecture: function(lectureCode){
    //subscribe to lectureCode
    this.state.ddp.sub("questions", [lectureCode]);
    this.state.ddp.sub("votes", [lectureCode]);
    this.setState({
      currentLectureCode: lectureCode,
      inLecture: true
    });
  },
  _askQuestion: function(question){
   var args = [];
    args.push(this.state.currentLectureCode);
    args.push(question);

    console.log("Asking question: ", [args]);
    //this.state.ddp.method("insertQuestion", ["s27h7", "what is up"], function(e, res){
      this.state.ddp.method("insertQuestion", args, function(e, res){
      if(e){
        console.log(JSON.stringify(e));
        /*
        {"error":400,"reason":"Match failed","message":"Match failed [400]","errorType":"Meteor.Error"}
        */
      }
      console.log("result: "+res);
    });
  },
  _vote: function(question){
    // Vote question

  },
  _login: function(){
    var options = {
      createGuest: true
    };
    this.state.ddp.method("login", [options], function(e, res){
      if(e){
        console.log(JSON.stringify(e));
        return;
      }
    console.log("Authenticated.");
    });

  },
  _leaveLecture: function(){
    // Leave current lecture
    if(this.state.inLecture){
      this.ddp.method()
    }
  },
  componentDidMount: function(){
    //Setup DDP event handlers
    this.state.ddp.on("connected", this.onConnected);
    this.state.ddp.on("added", this.onAdded);
    this.state.ddp.on("changed", this.onChanged);
    this.state.ddp.on("removed", this.onRemoved);
    this.state.ddp.on("result", this.onResult);

    // Subscribe / should be in onCOnnected?
    //this.state.ddp.sub("lecture");
    //this.state.ddp.sub("questions", ["s70t1"]);
    this.setState({ message: "Ready.."})
  },
  render: function(){
    var content = null;
    if(this.state.inLecture){
      content =  (<View style={styles.content}>
                    <Text>Status: {this.state.status}</Text>
                    <QuestionList questions={this.state.questions} voteHandler={this._vote}/>
                    <Input ask={this._askQuestion}/>
                  </View>);
    }else {
      content = (<View style={styles.content}>
                  <LectureSelector onClick={this._enterLecture} />
                </View>);
    }
      return (
        <View style={styles.mainContainer}>
          <Toolbar code={this.state.currentLectureCode} leaveHandler={this.state._leaveLecture}/>
            {content}
        </View>
      );

  }
});

var styles = StyleSheet.create({
  mainContainer: {
    flex: 1,
  },
  content:{
    flex: 1,
    backgroundColor:'#ebeef0',
  },
  header: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  question: {
    fontSize: 25,
  },

});



AppRegistry.registerComponent('GotItApp', () => GotItApp);
