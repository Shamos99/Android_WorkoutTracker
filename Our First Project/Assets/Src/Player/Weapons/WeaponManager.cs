using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class WeaponManager : MonoBehaviour
{
    // Public Variables
    public GameObject primarySlot;
    public GameObject secondarySlot;
    
    // Private Variables
    private bool primaryFlag;

    // Start is called before the first frame update
    void Start()
    {
        this.primaryFlag = true;
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetButtonDown("Primary") && !this.primaryFlag) {
            // TODO: Switch to primary weapon
            primarySlot.SetActive(true);
            secondarySlot.SetActive(false);
            this.primaryFlag = true;
        } else if (Input.GetButtonDown("Secondary") && this.primaryFlag) {
            // TODO: Switch to secondary weapon
            primarySlot.SetActive(false);
            secondarySlot.SetActive(true);
            this.primaryFlag = false;
        }
    }

    // Getters and Setters
    public void setPrimaryFlag(bool primaryFlag) {
        if (primaryFlag != null) {
            this.primaryFlag = primaryFlag;
        }
    }

    public bool getPrimaryFlag() { return this.primaryFlag; }
}
