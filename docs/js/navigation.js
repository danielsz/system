     function hideAll() {
       const els = document.querySelectorAll(".outline-text-2, .outline-2");
       els.forEach(el => { el.style.display = "none";});
     }
     function tocAll() {
       const els = document.querySelectorAll("#text-table-of-contents ul li a");
       els.forEach(el => { 
	 el.style.fontWeight = 300;});
     }
     
     window.onpopstate = function(event) {
       var el = document.querySelector("a[href='" + document.location.hash + "']");
       // console.log(el);
       hideAll();
       tocAll();
       el.style.fontWeight = 400;
       let section = document.location.hash.substring(1);
       const container = document.getElementById("outline-container-" + section);
       container.style.display = "block";
       for (let i = 0; i < container.children.length; i++) {
	 ( i === 0 ) ? container.children[i].style.display = "none" : container.children[i].style.display = "block";
       }
     }
