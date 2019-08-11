source("./common/constants.r")


decide_whether_energy_has_stopped_decreasing_or_not = function() {
    
    if (count >= MYPUSH_QUEUE_DEPTH && estimate < ENERGY_DROP_LIMIT) { 
        energy_has_not_stopped_decreasing = FALSE
    }
    
    if (count <= ENERGY_MINIMIZER_ITERATIONS_LIMIT) {
        energy_has_not_stopped_decreasing = FALSE
    }
    
    count = count + 1 # global variable defined in main_presets.r
}