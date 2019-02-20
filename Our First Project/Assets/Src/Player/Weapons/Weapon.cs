using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Weapon : MonoBehaviour
{

    //Weapon Atttributes
    public Transform gun_design;
    public float weapon_damage;
    public float fire_rate;
    public int magazine_size;
    public int current_magazine;
    public int total_bullets;
    public int range;
    public float reload_time;

    public Text ui;

    //Reference to Player
    public GameObject fetchplayer;

    // Flags
    private bool isFire;
    private bool isRealoading;
    private LineRenderer laserLine;

    //Setters
    void set_damage(int x)
    {
        weapon_damage = x;
    }

    void set_firerate(float x)
    {
        fire_rate = x;
    }

    void set_magazine(int x)
    {
        magazine_size = x;
    }

    void set_totalbullets(int x)
    {
        total_bullets = x;
    }

    void set_design(Transform x)
    {
        gun_design = x;
    }

    void set_range(int x)
    {
        range = x;
    }



    // Start is called before the first frame update
    void Start()
    {
        this.isFire = false;
        this.isRealoading = false;
        this.laserLine = GetComponent<LineRenderer>();
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetMouseButton(0) && !isFire && !isRealoading)
        {

            this.isFire = true;
            StartCoroutine(shootGun());
        }

        if (Input.GetButtonDown("Reload") && !isRealoading)
        {
            StartCoroutine(reload());


        }
        fetchplayer = GameObject.Find("FirstPersonCharacter");

        ui.text = current_magazine.ToString();

    }

    private IEnumerator reload()
    {
        this.isRealoading = true;
        yield return new WaitForSeconds(reload_time);

        if (total_bullets>=magazine_size)
        {
            current_magazine = magazine_size;
            total_bullets -= magazine_size;
        }
        else
        {
            current_magazine = total_bullets;
            total_bullets = 0;
        }

        this.isRealoading = false;
    }

    private IEnumerator shootGun() {
        if (current_magazine > 0) {
            float singleFireDuration = 1/fire_rate;

            Vector3 pos = fetchplayer.transform.position;
            Vector3 dir = fetchplayer.transform.forward;

            RaycastHit hit;
            this.laserLine.SetPosition(0, pos);

            if (Physics.Raycast(pos, dir, out hit, range)) {
                this.laserLine.SetPosition(1, hit.point);
            } else {
                this.laserLine.SetPosition(1, dir * range);
            }


            current_magazine--;
            yield return new WaitForSeconds(singleFireDuration);
            this.isFire = false;
        }
        else {
            // No ammo in magazine
            // you can play an audioclip of empty magazine, that clicky noise
            yield return new WaitForSeconds(0.1f);
            this.isFire = false;
        }
    }
}
