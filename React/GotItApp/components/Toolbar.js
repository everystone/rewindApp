
'use strict';

var React = require('react-native');
var {
  View,
  Text,
  StyleSheet
} = React;

var Toolbar = React.createClass({
  render: function(){
    return(
      <View>
       <View style={styles.toolbar}>
           <Text style={styles.toolbarButton}>{this.props.code}</Text>
           <Text style={styles.toolbarTitle}>GotIt</Text>
           <Text style={styles.toolbarButton}>Leave</Text>
       </View>
     </View>
    );
  }
});

var styles = StyleSheet.create({
  toolbar:{
      backgroundColor:'#ec971f',
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
      flex:1                //Step 3
  }
})

module.exports = Toolbar;
