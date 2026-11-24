import React, { useState } from "react";
import { SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, TouchableOpacity, View } from "react-native";

const C = { white:"#FFFFFF", ink:"#332719", brown:"#684A25", dark:"#4F3A23", rust:"#DF842C", gold:"#C9A227", muted:"#8B8175", line:"#EDE8E1", soft:"#F8F6F2", pale:"#F2EDE6" };
const shortcuts = [
  { icon:"⌕", title:"HN Navigatie", detail:"Ontdek landen en steden" },
  { icon:"✓", title:"Mijn planner", detail:"Houd je stappen bij" },
  { icon:"▤", title:"Kennisbank", detail:"Gidsen en praktische info" },
  { icon:"◎", title:"Community", detail:"Vind zusters en ervaringen" },
];

export default function App() {
  const [activeTab, setActiveTab] = useState("Home");
  return <SafeAreaView style={s.safe}>
    <StatusBar barStyle="dark-content" backgroundColor={C.white} />
    <View style={s.topbar}>
      <View><Text style={s.brand}>HIJRAH NETWERK</Text><Text style={s.brandSub}>VAN ORIËNTATIE TOT INTEGRATIE</Text></View>
      <TouchableOpacity style={s.avatar} accessibilityLabel="Profiel"><Text style={s.avatarText}>HN</Text></TouchableOpacity>
    </View>
    <ScrollView contentContainerStyle={s.content} showsVerticalScrollIndicator={false}>
      <View style={s.welcome}>
        <View style={s.welcomeTop}>
          <View style={s.welcomeLabel}><View style={s.liveDot}/><Text style={s.welcomeLabelText}>JOUW PERSOONLIJKE PLEK</Text></View>
          <Text style={s.welcomeEyebrow}>Assalamu alaikum</Text>
          <Text style={s.welcomeTitle}>Welkom bij{"\n"}Hijrah Netwerk</Text>
          <Text style={s.welcomeCopy}>Alles wat je nodig hebt om je volgende stappen rustig en overzichtelijk voor te bereiden.</Text>
          <TouchableOpacity style={s.primaryButton} onPress={() => setActiveTab("Planner")}><Text style={s.primaryButtonText}>Verder met mijn planner</Text><Text style={s.buttonArrow}>↗</Text></TouchableOpacity>
        </View>
        <View style={s.welcomeBottom}>
          <Text style={s.welcomeStat}>JOUW TRAJECT</Text><Text style={s.welcomeStatValue}>Stap voor stap</Text>
          <View style={s.progressTrack}><View style={s.progressFill}/></View><Text style={s.progressText}>Je voortgang verschijnt hier</Text>
        </View>
      </View>
      <View style={s.sectionHeading}><View><Text style={s.sectionEyebrow}>ONTDEK HN</Text><Text style={s.sectionTitle}>Waar wil je beginnen?</Text></View><TouchableOpacity><Text style={s.seeAll}>Bekijk alles ↗</Text></TouchableOpacity></View>
      <View style={s.shortcutGrid}>{shortcuts.map((item,index)=><TouchableOpacity key={item.title} style={[s.shortcut,index===0&&s.shortcutFeatured]} onPress={()=>setActiveTab(item.title)}>
        <View style={[s.shortcutIcon,index===0&&s.shortcutIconFeatured]}><Text style={[s.shortcutIconText,index===0&&s.shortcutIconTextFeatured]}>{item.icon}</Text></View>
        <Text style={s.shortcutTitle}>{item.title}</Text><Text style={s.shortcutDetail}>{item.detail}</Text><Text style={s.shortcutArrow}>↗</Text>
      </TouchableOpacity>)}</View>
      <View style={s.sectionHeading}><View><Text style={s.sectionEyebrow}>VOOR JOU</Text><Text style={s.sectionTitle}>Handig om te weten</Text></View></View>
      <TouchableOpacity style={s.infoCard}><View style={s.infoIcon}><Text style={s.infoIconText}>✦</Text></View><View style={s.infoTextWrap}><Text style={s.infoTitle}>Begin met een goede oriëntatie</Text><Text style={s.infoCopy}>Bekijk waar je op kunt letten bij het onderzoeken van een land of stad.</Text></View><Text style={s.infoArrow}>›</Text></TouchableOpacity>
      <View style={s.footerNote}><Text style={s.footerNoteText}>Van zuster tot zuster, van emigrant tot emigrant.</Text></View>
    </ScrollView>
    <View style={s.tabbar}>{[["Home","⌂"],["Navigatie","⌕"],["Planner","✓"],["Community","◎"],["Profiel","○"]].map(([label,icon])=><TouchableOpacity key={label} style={s.tab} onPress={()=>setActiveTab(label)}>
      <Text style={[s.tabIcon,activeTab===label&&s.tabActive]}>{icon}</Text><Text style={[s.tabLabel,activeTab===label&&s.tabLabelActive]}>{label}</Text>{activeTab===label&&<View style={s.tabIndicator}/>}
    </TouchableOpacity>)}</View>
  </SafeAreaView>;
}

const s=StyleSheet.create({
 safe:{flex:1,backgroundColor:C.white}, topbar:{height:72,paddingHorizontal:22,flexDirection:"row",alignItems:"center",justifyContent:"space-between",borderBottomWidth:1,borderBottomColor:C.line,backgroundColor:C.white},
 brand:{fontSize:15,fontWeight:"800",letterSpacing:2.1,color:C.brown},brandSub:{fontSize:8,fontWeight:"700",letterSpacing:1.25,color:C.muted,marginTop:4},
 avatar:{width:42,height:42,borderRadius:15,backgroundColor:C.pale,alignItems:"center",justifyContent:"center",borderWidth:1,borderColor:"#E7DCCB"},avatarText:{fontSize:12,fontWeight:"800",color:C.brown,letterSpacing:1},
 content:{paddingHorizontal:20,paddingTop:22,paddingBottom:32},welcome:{borderRadius:25,overflow:"hidden",backgroundColor:C.dark,shadowColor:"#352719",shadowOpacity:0.18,shadowRadius:18,shadowOffset:{width:0,height:9},elevation:7},
 welcomeTop:{padding:23,paddingBottom:24},welcomeLabel:{flexDirection:"row",alignItems:"center",alignSelf:"flex-start",paddingHorizontal:11,paddingVertical:7,borderRadius:30,backgroundColor:"rgba(255,255,255,0.11)",marginBottom:22},
 liveDot:{width:6,height:6,borderRadius:3,backgroundColor:"#E7B75B",marginRight:7},welcomeLabelText:{fontSize:8,fontWeight:"800",letterSpacing:1.3,color:"#F1E6D5"},
 welcomeEyebrow:{fontSize:13,color:"#E6CBA9",marginBottom:8},welcomeTitle:{fontSize:30,lineHeight:36,fontWeight:"700",letterSpacing:0.1,color:C.white},
 welcomeCopy:{fontSize:13,lineHeight:20,color:"#E1D8CC",marginTop:12,maxWidth:290},primaryButton:{height:48,borderRadius:15,backgroundColor:C.rust,marginTop:22,paddingHorizontal:16,flexDirection:"row",alignItems:"center",justifyContent:"space-between"},
 primaryButtonText:{fontSize:12,fontWeight:"800",color:C.white},buttonArrow:{fontSize:18,color:C.white},welcomeBottom:{paddingHorizontal:23,paddingVertical:17,backgroundColor:"rgba(255,255,255,0.07)",borderTopWidth:1,borderTopColor:"rgba(255,255,255,0.12)"},
 welcomeStat:{fontSize:8,letterSpacing:1.4,fontWeight:"800",color:"#C9BBA9"},welcomeStatValue:{fontSize:13,fontWeight:"700",color:C.white,marginTop:5},progressTrack:{height:5,borderRadius:4,backgroundColor:"rgba(255,255,255,0.18)",marginTop:13,overflow:"hidden"},
 progressFill:{width:"18%",height:5,borderRadius:4,backgroundColor:"#DDA34A"},progressText:{fontSize:10,color:"#C9BBA9",marginTop:7},
 sectionHeading:{marginTop:29,marginBottom:14,flexDirection:"row",alignItems:"flex-end",justifyContent:"space-between"},sectionEyebrow:{fontSize:8,fontWeight:"800",letterSpacing:1.5,color:C.rust,marginBottom:5},
 sectionTitle:{fontSize:19,fontWeight:"800",color:C.ink,letterSpacing:-0.4},seeAll:{fontSize:10,fontWeight:"700",color:C.brown,paddingBottom:2},
 shortcutGrid:{flexDirection:"row",flexWrap:"wrap",justifyContent:"space-between",rowGap:12},shortcut:{width:"48.2%",minHeight:145,padding:15,borderRadius:20,backgroundColor:C.white,borderWidth:1,borderColor:C.line,shadowColor:"#3B2B1B",shadowOpacity:0.055,shadowRadius:12,shadowOffset:{width:0,height:4},elevation:2},
 shortcutFeatured:{backgroundColor:C.pale,borderColor:"#E6D7C3"},shortcutIcon:{width:37,height:37,borderRadius:13,backgroundColor:C.soft,alignItems:"center",justifyContent:"center",marginBottom:13},shortcutIconFeatured:{backgroundColor:C.brown},
 shortcutIconText:{fontSize:21,color:C.brown,fontWeight:"600"},shortcutIconTextFeatured:{color:C.white},shortcutTitle:{fontSize:13,fontWeight:"800",color:C.ink},shortcutDetail:{fontSize:10,lineHeight:15,color:C.muted,marginTop:5,maxWidth:130},
 shortcutArrow:{position:"absolute",right:14,top:15,fontSize:14,color:C.rust},infoCard:{borderRadius:19,padding:15,backgroundColor:C.soft,borderWidth:1,borderColor:C.line,flexDirection:"row",alignItems:"center"},
 infoIcon:{width:40,height:40,borderRadius:14,backgroundColor:"#EDE2D2",alignItems:"center",justifyContent:"center",marginRight:12},infoIconText:{fontSize:18,color:C.brown},infoTextWrap:{flex:1},
 infoTitle:{fontSize:12,fontWeight:"800",color:C.ink},infoCopy:{fontSize:10,lineHeight:15,color:C.muted,marginTop:4},infoArrow:{fontSize:24,color:C.rust,marginLeft:8},
 footerNote:{paddingVertical:23,alignItems:"center"},footerNoteText:{fontSize:10,fontStyle:"italic",color:C.muted},tabbar:{height:72,paddingHorizontal:8,borderTopWidth:1,borderTopColor:C.line,backgroundColor:C.white,flexDirection:"row",justifyContent:"space-around",alignItems:"center"},
 tab:{flex:1,alignItems:"center",justifyContent:"center",height:60},tabIcon:{fontSize:20,color:"#A49A8D"},tabActive:{color:C.rust},tabLabel:{fontSize:9,color:"#958A7E",marginTop:4,fontWeight:"600"},tabLabelActive:{color:C.brown,fontWeight:"800"},
 tabIndicator:{position:"absolute",top:0,width:18,height:3,borderBottomLeftRadius:3,borderBottomRightRadius:3,backgroundColor:C.rust}
});
