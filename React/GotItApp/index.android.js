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
  TextInput,
  View,
  TouchableHighlight,
  ListView
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
        dataSource: new ListView.DataSource({
          rowHasChanged: (row1, row2) => row1 !== row2,
          }),


    };
  },
  onConnected: function(){
    this.setState({status: "Connected"});
    //Subscribe to LectureCodes
    this.login();

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
      _questions[data.id] = data.fields;
        this.setState({
          questions: _questions,
          status: data.id,
          dataSource: this.state.dataSource.cloneWithRows(this.state.questions)
        });
        break;

      case "votes":

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
  enterLecture: function(lectureCode){
    //subscribe to lectureCode
    this.state.ddp.sub("questions", [lectureCode]);
    this.setState({
      currentLectureCode: lectureCode,
      inLecture: true
    });
  },
  askQuestion: function(question){
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
        * This fails if user is not authenticated.
        After the package account-password was removed from the backend, I don't know how to authenticate anymore.
        */
      }
      console.log("result: "+res);
    });
  },
  login: function(){
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
  leaveLecture: function(){
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
                    <QuestionList dataSource={this.state.dataSource}/>
                    <Input ask={this.askQuestion}/>
                  </View>);
    }else {
      content = (<View style={styles.content}>
                  <LectureSelector onClick={this.enterLecture} />
                </View>);
    }
      return (
        <View style={styles.mainContainer}>
          <Toolbar code={this.state.currentLectureCode} leaveHandler={this.state.leaveLecture}/>
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
