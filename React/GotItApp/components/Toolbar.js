
'use strict';

var React = require('react-native');
var {
  View,
  Text,
  StyleSheet,
  TouchableHighlight
} = React;

var Toolbar = React.createClass({
  onLeaveClick: function(e){
    this.props.leaveHandler();
  },
  render: function(){
    return(
      <View>
       <View style={styles.toolbar}>
           <Text style={styles.toolbarButton}>{this.props.code}</Text>
           <Text style={styles.toolbarTitle}>GotIt</Text>
           <TouchableHighlight onPress={this.onLeaveClick}>
           <Text style={styles.toolbarButton}>Leave</Text>
           </TouchableHighlight>
       </View>
     </View>
    );
  }
});

var styles = StyleSheet.create({
  toolbar:{
      backgroundColor:'#454453',
      paddingTop:30,
      paddingBottom:10,
      flexDirection:'row'    //Step 1
  },
  toolbarButton:{
      width: 50,            //Step 2
      color:'#fff',
      textAlign:'center'
  },
  toolbarTitle:{
      color:'#fff',
      textAlign:'center',
      fontWeight:'bold',
      fontSize: 16,
      flex:1                //Step 3
  }
})

module.exports = Toolbar;
